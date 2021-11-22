package services

import configurations.interfaces.IConnectionToDb
import dtos.authorization.*
import dtos.failed
import dtos.login.LoginBy
import dtos.login.LoginRequest
import dtos.signup.SignupResponse
import dtos.signup.SignupResponseFailed
import dtos.succeeded
import exceptions.ServerErrorException
import helper.isEmailFormatted
import helper.isStrongPassword
import helper.maskEmail
import io.ktor.http.*
import managers.interfaces.IEmailManager
import managers.interfaces.IPasswordManager
import managers.interfaces.ITokenManager
import models.profile.Author
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repositories.interfaces.IAuthorRepository
import repositories.interfaces.IEmailVerifyCodeRepository
import responseData.PasswordResetResponseData
import serialized.CreateAuthorRequest
import serialized.LoginByEmailRequest
import serialized.LoginByUsernameRequest

//private val logger = KotlinLogging.logger {}

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
            val authorId = authorRepository.insertAuthor(request)
            authorId ?: throw ServerErrorException("Failed to create author", this::class.java)
            passwordManager.setNewPassword(request.password, authorId)
            emailManager.welcomeNewAuthor(request.email)
            val tokens = tokenManager.generateTokens(authorId)

            return SignupResponse().succeeded(HttpStatusCode.Created, tokens)
        }
    }

    fun login(request: LoginByEmailRequest): LoginResponse {
        return login(
            LoginRequest(credential = request.email, password = request.password, loginBy = LoginBy.Email)
        )
    }

    fun login(request: LoginByUsernameRequest): LoginResponse {
        return login(
            LoginRequest(credential = request.username, password = request.password, loginBy = LoginBy.Username)
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
        return tokenManager.refreshAccessToken(refreshToken, authorId)
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

        emailManager.welcomeNewAuthor(author.email)

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

        if (!passwordManager.validatePassword(request.password, author.id))
            return LoginResponse().failed(LoginResponseFailed.InvalidPassword)

        val tokenResponse = tokenManager.generateTokens(author.id)
        return LoginResponse().succeeded(HttpStatusCode.OK, tokenResponse)
    }
}