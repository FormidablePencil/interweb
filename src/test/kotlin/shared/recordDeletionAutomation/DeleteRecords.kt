package shared.recordDeletionAutomation

import com.idealIntent.configurations.AppEnv
import com.idealIntent.configurations.DIHelper
import com.idealIntent.repositories.PasswordRepository
import com.idealIntent.repositories.RefreshTokenRepository
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import com.idealIntent.repositories.profile.AuthorRepository
import models.profile.AccountsModel
import models.profile.AuthorDetails
import models.profile.AuthorsModel
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
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

    DeleteRecords().deleteAccountsByUsername("Martini9")
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
    private val carouselOfImagesRepository: CarouselOfImagesRepository by inject()
    private val deleteCarouselBasicImageUtil: DeleteCarouselBasicImageUtil by inject()
    private val appEnv: AppEnv by inject()
    private val defaultIdValue = 90000000

    /**
     * Delete accounts by username
     *
     * @param username username of author to find and delete by.
     */
    fun deleteAccountsByUsername(username: String) {
        val authorId = authorRepository.getByUsername(username)?.id
            ?: throw Exception("Did not find author by username of $username")
        deleteAccount(authorId)
    }

    /**
     * Delete account and all records that are referencing author.id thought it doesn't
     * delete other record that belong to author like compositions and are associated to
     * privileges are standalone. So in this instance privileged_author_to_composition will
     * be deleted but the composition and privilege source will not. This class is only for
     * testing purposes.
     *
     * @param authorId Author id to delete by.
     */
    fun deleteAccount(authorId: Int) {
        // region collections and compositions related
        deleteCarouselBasicImageUtil.deleteAllOfAuthorsCarouselBasicImage(authorId)
        // endregion

        // region authentication related
        refreshTokenRepository.delete(authorId)
        passwordRepository.delete(authorId)
        appEnv.database.delete(AccountsModel) { it.authorId eq authorId }
        appEnv.database.delete(AuthorDetails) { it.authorId eq authorId }
        appEnv.database.delete(AuthorsModel) { it.id eq authorId }
        // endregion
    }


}
