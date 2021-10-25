package domainServices

import dto.author.CreateAuthorRequest
import dto.signup.SignupResult
import helper.DbHelper
import helper.isStrongPassword
import managers.IAuthorizationManager
import managers.ITokenManager
import repositories.IAuthorRepository

class SignupDomainService(
    private val dbHelper: DbHelper,
    private val authorizationManager: IAuthorizationManager,
    private val authorRepository: IAuthorRepository,
    private val tokenManager: ITokenManager,
) {
    fun signup(request: CreateAuthorRequest): SignupResult {
        // TODO: Unit tests all the steps before doing integration tests unless I have to

        dbHelper.database.useTransaction {
            if (!isStrongPassword(request.password)) throw Exception("Not strong enough password")
            checkNotNull(authorRepository.getByEmail(request.email)) { "email taken" }
            checkNotNull(authorRepository.getByUsername(request.email)) { "username taken" }

            val authorId = authorRepository.createAuthor(request)
            val tokens = tokenManager.generateTokens(authorId, request.username)
            authorizationManager.setNewPassword(request.password)

            //TODO: send message through 3rd party postMark welcoming new author

            return SignupResult(authorId, tokens)
        }
    }
}