package integrationTests.auth.flows

import com.idealIntent.dtos.CreateAuthorRequest
import com.idealIntent.dtos.auth.LoginByUsernameRequest
import com.idealIntent.repositories.profile.AccountRepository
import com.idealIntent.repositories.profile.AuthorRepository
import models.profile.IAccountEntity
import org.koin.test.inject
import shared.testUtils.BehaviorSpecFlow

class AuthUtilities : BehaviorSpecFlow() {
    private val authorRepository: AuthorRepository by inject()
    private val accountRepository: AccountRepository by inject()

    fun getAuthorIdByUsername(username: String = createAuthorRequest.username): Int {
        return authorRepository.getByUsername(username)?.id
            ?: throw Exception("failed to get authorId by username. Author doesn't exist by that username")
    }

    fun getAccountByEmail(email: String = createAuthorRequest.email): IAccountEntity {
        return accountRepository.getByEmail(email)
            ?: throw Exception("failed to get user's Account by email. Account doesn't exist by that username")
    }

    fun getAccountByUsername(username: String = createAuthorRequest.username): IAccountEntity {
        val id = getAuthorIdByUsername(username)
        return accountRepository.getById(id)
            ?: throw Exception("failed to get user's Account by id. Account doesn't exist by that id")
    }

    companion object {
        val createAuthorRequest = CreateAuthorRequest(
            "6saberryyTest1235@gmail.com9", "CherryCas6as", "Alex", "Formidable!56", "Martini9"
        )
        val loginByUsernameRequest = LoginByUsernameRequest(
            username = createAuthorRequest.username,
            password = createAuthorRequest.password,
        )
    }
}