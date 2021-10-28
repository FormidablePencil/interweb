package domainServices

import configurations.interfaces.IConnectionToDb
import dtos.author.CreateAuthorRequest
import dtos.failed
import dtos.signup.SignupResult
import dtos.signup.SignupResultError
import dtos.succeeded
import helper.isEmailFormatted
import helper.isStrongPassword
import managers.interfaces.IAuthorizationManager
import managers.interfaces.ITokenManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repositories.interfaces.IAuthorRepository

class SignupDomainService(
    private val authorizationManager: IAuthorizationManager,
    private val authorRepository: IAuthorRepository,
    private val tokenManager: ITokenManager,
) : KoinComponent {
    private val connectionToDb: IConnectionToDb by inject();

    fun signup(request: CreateAuthorRequest): SignupResult {
        if (!isStrongPassword(request.password))
            return SignupResult().failed(SignupResultError.WeakPassword, "Weak password.")
        if (!isEmailFormatted(request.email))
            return SignupResult().failed(
                SignupResultError.InvalidEmailFormat, "Email provided is not formatted as such."
            )
        if (authorRepository.getByEmail(request.email) != null)
            return SignupResult().failed(SignupResultError.ServerError, "Email taken.")
        if (authorRepository.getByUsername(request.username) != null)
            return SignupResult().failed(SignupResultError.ServerError, "Username taken.")

        connectionToDb.database.useTransaction {
            val authorId = authorRepository.createAuthor(request)
            val tokens = tokenManager.generateTokens(authorId, request.username)
            authorizationManager.setNewPassword(request.password)

            //TODO: send message through 3rd party postMark welcoming the new author

            return SignupResult(authorId).succeeded()
        }
    }
}