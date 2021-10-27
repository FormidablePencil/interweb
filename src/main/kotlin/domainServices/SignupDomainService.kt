package domainServices

import configurations.IConnectionToDb
import dto.ApiRequestResult
import dto.author.CreateAuthorRequest
import dto.signup.SignupResult
import dto.signup.SignupResultError
import helper.isEmailFormatted
import helper.isStrongPassword
import managers.IAuthorizationManager
import managers.ITokenManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repositories.IAuthorRepository

class SignupDomainService(
    private val authorizationManager: IAuthorizationManager,
    private val authorRepository: IAuthorRepository,
    private val tokenManager: ITokenManager,
) : KoinComponent {
    private val connectionToDb: IConnectionToDb by inject();

    fun signup(request: CreateAuthorRequest): SignupResult {
//        var signupResult = SignupResult()

        connectionToDb.database.useTransaction {
            if (!isStrongPassword(request.password)) throw Exception("Not strong enough password")
            if (!isEmailFormatted(request.email)) throw Exception("Not an email provided")



//            if (authorRepository.getByEmail(request.email) != null)
//                return result2.authorId
            check(authorRepository.getByUsername(request.username) != null) { "username taken" }

            val authorId = authorRepository.createAuthor(request)
            val tokens = tokenManager.generateTokens(authorId, request.username)
            authorizationManager.setNewPassword(request.password)

            //TODO: send message through 3rd party postMark welcoming the new author

            return SignupResult(authorId)
//            return SignupResult(authorId, tokens)
        }
    }
}