package services

import configurations.interfaces.IConnectionToDb
import dtos.author.CreateAuthorRequest
import dtos.authorization.*
import dtos.signup.SignupResult
import dtos.signup.SignupResultError
import helper.*
import managers.interfaces.IPasswordManager
import managers.interfaces.ITokenManager
import models.profile.Author
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repositories.interfaces.IAuthorRepository

class AuthorizationService(
    private val authorRepository: IAuthorRepository,
    private val tokenManager: ITokenManager,
    private val emailService: EmailService,
    private val passwordManager: IPasswordManager,
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
            val tokens = tokenManager.genTokensOnSignup(authorId)
            setNewPasswordForSignup(request.password)

            //TODO: send message through 3rd party postMark welcoming the new author

            return SignupResult(authorId).succeeded()
        }
    }

    fun login(email: String, password: String): LoginResult {
        val errorResponseMessage = "Invalid credentials"

        val author: Author? = authorRepository.getByEmail(email)
        if (author?.id == null)
            return LoginResult().failed(LoginResultError.InvalidEmail, errorResponseMessage)

        return if (passwordManager.validatePassword(password, author.id))
            LoginResult(author.id).succeeded()
        else LoginResult().failed(LoginResultError.InvalidPassword, errorResponseMessage)
    }

    fun refreshAccessToken(refreshToken: String, authorId: Int): TokensResult {
        return tokenManager.refreshAccessToken(refreshToken, authorId)
    }

    fun requestPasswordReset(username: String?, email: String?): RequestPasswordResetResult {
        val author: Author?

        if (!username.isNullOrEmpty()) {
            author = authorRepository.getByUsername(username)
            if (author == null) return RequestPasswordResetResult().failed(RequestPasswordResetResultError.AccountNotFoundByGivenUsername)
        } else if (!email.isNullOrEmpty()) {
            author = authorRepository.getByEmail(email)
            if (author == null) return RequestPasswordResetResult().failed(RequestPasswordResetResultError.AccountNotFoundByGivenEmail)
        } else
            return RequestPasswordResetResult().failed(RequestPasswordResetResultError.NeitherUsernameNorEmailProvided)

        emailService.sendCreatedAccount(author.id)

        val maskedEmail = maskEmail(author.email)
        return RequestPasswordResetResult(maskedEmail).succeeded()
    }

    fun setNewPasswordForSignup(password: String): Int {
        return passwordManager.setNewPassword(password)
    }

    fun resetPasswordByEmail(oldPassword: String, newPassword: String, emailCode: String): ResetPasswordResult {

        TODO("validate emailCode") // ideally, a confirmation should be sent to mail and the link to reset password

        // get authorId base off of emailCode - I wonder if the email code is a jwt token??? - It could work
        val authorId = 1

        return passwordManager.resetPassword(oldPassword, newPassword, authorId)
    }
}

// tokens must be saved in db
// tokens must be sent from the client via bearer
// and validated everytime before giving access to data
