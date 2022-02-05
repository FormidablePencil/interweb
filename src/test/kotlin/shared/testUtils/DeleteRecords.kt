package shared.testUtils

import com.idealIntent.configurations.AppEnv
import com.idealIntent.configurations.DIHelper
import com.idealIntent.dtos.CreateAuthorRequest
import com.idealIntent.models.compositions.basicCollections.texts.TextCollectionsModel
import com.idealIntent.models.compositions.basicCollections.texts.TextToCollectionsModel
import com.idealIntent.models.compositions.basicCollections.texts.TextsModel
import com.idealIntent.repositories.PasswordRepository
import com.idealIntent.repositories.RefreshTokenRepository
import com.idealIntent.repositories.profile.AuthorRepository
import models.profile.AccountsModel
import models.profile.AuthorDetails
import models.profile.AuthorsModel
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.ktorm.dsl.*
import shared.DITestHelper

fun main() {
    startKoin { modules(listOf(DIHelper.CoreModule, DITestHelper.FlowModule)) }

//    privilegedAuthors.map {
//        val createAuthorRequest = CreateAuthorRequest(
//            email = it.username + "@gmail.com",
//            firstname = it.username,
//            lastname = it.username,
//            password = it.username + "Q123!",
//            username = it.username
//        )
//        DeleteRecords().deleteAccountsByUsername(createAuthorRequest.username)
//    }
}

/**
 * A list of delete records commands.
 * Typically, for when you forget to wrap a rollback to revert test and accounts are created. This is
 * useful because multiple different records may be created and deleting everything by hand is tedious especially parent
 * records that are referenced by others as foreign key.
 */
class DeleteRecords() : KoinTest {
    //    fun listeners() = listOf(KoinListener(listOf(DIHelper.CoreModule, DITestHelper.FlowModule)))
    private val refreshTokenRepository: RefreshTokenRepository by inject()
    private val passwordRepository: PasswordRepository by inject()
    private val authorRepository: AuthorRepository by inject()
    private val appEnv: AppEnv by inject()

    fun deleteAccountsByUsername(username: String) {
        val authorId = authorRepository.getByUsername(username)?.id
            ?: throw Exception("Did not find author by username of $username")
        deleteAccount(authorId)
    }

    fun deleteAccount(authorId: Int) {
        refreshTokenRepository.delete(authorId)
        passwordRepository.delete(authorId)
        appEnv.database.delete(AccountsModel) { it.authorId eq authorId }
        appEnv.database.delete(AuthorDetails) { it.authorId eq authorId }
        appEnv.database.delete(AuthorsModel) { it.id eq authorId }
    }

    fun deleteTextRecords(collectionId: Int) {
        // get text ids before deleting
        val textIds = appEnv.database.from(TextToCollectionsModel)
            .select(TextToCollectionsModel.textId)
            .where { TextToCollectionsModel.collectionId eq collectionId }
            .map { it[TextToCollectionsModel.textId]!! }

        // delete association between records and collection then collection and records
        appEnv.database.delete(TextToCollectionsModel) { it.collectionId eq collectionId }
        appEnv.database.delete(TextCollectionsModel) { it.id eq collectionId }
        textIds.map { appEnv.database.delete(TextsModel) { it.id eq it.id } }
    }
}
