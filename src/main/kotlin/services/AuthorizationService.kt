package services

import configurations.interfaces.IConnectionToDb
import dtos.author.CreateAuthorRequest
import dtos.authorization.*
import dtos.failed
import dtos.login.LoginBy
import dtos.login.LoginByEmailRequest
import dtos.login.LoginByUsernameRequest
import dtos.login.LoginRequest
import dtos.signup.SignupResponse
import dtos.signup.SignupResponseFailed
import dtos.succeeded
import exceptions.ServerErrorException
import exceptions.ServerFailed
import helper.*
import io.ktor.http.*
import managers.interfaces.IEmailManager
import managers.interfaces.IPasswordManager
import managers.interfaces.ITokenManager
import models.profile.Author
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repositories.interfaces.IAuthorRepository
import repositories.interfaces.IEmailVerifyCodeRepository
import responseData.PasswordResetResponseData

private val logger = KotlinLogging.logger {}

class AuthorizationService(
    private val authorRepository: IAuthorRepository,
    private val tokenManager: ITokenManager,
    private val emailManager: IEmailManager,
    private val passwordManager: IPasswordManager,
    private val emailVerifyCodeRepository: IEmailVerifyCodeRepository,
) : KoinComponent {
    private val connectionToDb: IConnectionToDb by inject();

    fun signup(request: CreateAuthorRequest): SignupResponse {
        if (!isStrongPassword(request.password))
            return SignupResponse().failed(SignupResponseFailed.WeakPassword)
        if (!isEmailFormatted(request.email))
            return SignupResponse().failed(SignupResponseFailed.InvalidEmailFormat)

        if (authorRepository.getByEmail(request.email) is Author)
            return SignupResponse().failed(SignupResponseFailed.EmailTaken)
        if (authorRepository.getByUsername(request.username) is Author)
            return SignupResponse().failed(SignupResponseFailed.UsernameTaken)

        connectionToDb.database.useTransaction {
            val authorId = authorRepository.createAuthor(request)
            authorId?: throw ServerErrorException(ServerFailed.FailedToCreateAuthor, this::class.java)
            passwordManager.setNewPassword(request.password) // TODO we really need to swap ktorm for exposed asap
            emailManager.sendValidateEmail(request.email)
            val tokens = tokenManager.generateTokens(authorId)

            return SignupResponse().succeeded(HttpStatusCode.MultiStatus, tokens)
        }
    }

    fun login(request: LoginByEmailRequest): LoginResponse {
        return login(LoginRequest(credential = request.email, password = request.password, loginBy = LoginBy.Email))
    }

    fun login(request: LoginByUsernameRequest): LoginResponse {
        return login(
            LoginRequest(
                credential = request.username,
                password = request.password,
                loginBy = LoginBy.Username
            )
        )
    }

    fun verifyEmailCode(request: VerifyEmailCodeRequest): VerifyEmailCodeResponse {
        val authorId = 2 // todo get authorId from token header

        val codeDb = emailVerifyCodeRepository.get(authorId)
            ?: return VerifyEmailCodeResponse().failed(VerifyEmailCodeResponseFailed.DoesNotExistEmailCode)

        return if (codeDb == request.code)
            VerifyEmailCodeResponse().succeeded(HttpStatusCode.OK)
        else VerifyEmailCodeResponse().failed(VerifyEmailCodeResponseFailed.InvalidEmailCode)
    }

    fun refreshAccessToken(refreshToken: String, authorId: Int): TokensResponse {
        val tokenResponseData = tokenManager.refreshAccessToken(refreshToken, authorId)
        return TokensResponse().succeeded(HttpStatusCode.OK, tokenResponseData)
    }

    fun requestPasswordReset(username: String?, email: String?): RequestPasswordResetResponse {
        val author: Author?

        // todo refactor to use a where. logic does it well
        if (!username.isNullOrEmpty()) {
            author = authorRepository.getByUsername(username)
            if (author !is Author) return RequestPasswordResetResponse.failed(RequestPasswordResetResponseFailed.AccountNotFoundByGivenUsername)
        } else if (!email.isNullOrEmpty()) {
            author = authorRepository.getByEmail(email)
            if (author !is Author) return RequestPasswordResetResponse.failed(RequestPasswordResetResponseFailed.AccountNotFoundByGivenEmail)
        } else
            return RequestPasswordResetResponse.failed(RequestPasswordResetResponseFailed.NeitherUsernameNorEmailProvided)

        emailManager.sendValidateEmail(author.email)

        val maskedEmail = maskEmail(author.email)
        return RequestPasswordResetResponse.succeeded(HttpStatusCode.OK, PasswordResetResponseData(maskedEmail))

    }

    fun resetPasswordByEmail(oldPassword: String, newPassword: String, emailCode: String): ResetPasswordResponse {

        TODO("validate emailCode") // ideally, a confirmation should be sent to mail and the link to reset password

        // get authorId base off of emailCode - I wonder if the email getStatusCode is a jwt token??? - It could work
        val authorId = 1

        return passwordManager.resetPassword(oldPassword, newPassword, authorId)
    }

    private fun login(request: LoginRequest): LoginResponse {
        val author = when (request.loginBy) {
            LoginBy.Email -> authorRepository.getByEmail(request.credential)
                ?: return LoginResponse().failed(LoginResponseFailed.InvalidEmail)
            LoginBy.Username -> authorRepository.getByUsername(request.credential)
                ?: return LoginResponse().failed(LoginResponseFailed.InvalidUsername)
        }

        if (validatePassword(request.password, author.id))
            return LoginResponse().failed(LoginResponseFailed.InvalidPassword)

        val tokenResponse = tokenManager.generateTokens(author.id)
        return LoginResponse().succeeded(HttpStatusCode.OK, tokenResponse)
    }

    private fun validatePassword(password: String, authorId: Int): Boolean {
        return passwordManager.validatePassword(password, authorId)
    }
}
// Authorization features
//  authorize restricted data with valid token from bearer
//  login to get access token (and refresh token)
//  reset password to get access token
//  create account to get access token

// Token
//  tokens must be saved in db
//  tokens must be sent from the client via bearer
//  and validated everytime before giving access to data
//  The refresh token should be stored to validate that the user has not reset their password and wiped all access from all devices

// Models/tables
//  tokens - authorization purposes
//  author - resource association purposes
//  password - authentication purposes
//  password_reset_code

// Why
//  we need to restrict resources and modification privileges to everyone outside
//  once this Authorization is built, we can have users use the app while new features are still being built