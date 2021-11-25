package services

import configurations.interfaces.IConnectionToDb
import dtos.authorization.*
import dtos.failed
import dtos.login.LoginBy
import dtos.login.LoginRequest
import dtos.responseData.PasswordResetResponseData
import dtos.signup.SignupResponse
import dtos.signup.SignupResponseFailed
import dtos.succeeded
import exceptions.ServerErrorException
import helper.isEmailFormatted
import helper.isStrongPassword
import helper.maskEmail
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import managers.interfaces.IEmailManager
import managers.interfaces.IPasswordManager
import managers.interfaces.ITokenManager
import models.profile.Author
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repositories.interfaces.IAuthorRepository
import repositories.interfaces.IEmailVerifyCodeRepository
import serialized.CreateAuthorRequest
import serialized.LoginByEmailRequest
import serialized.LoginByUsernameRequest


fun main() {
    // how do I make this code run in parallel
    runBlocking {
        launch {
            delay(2000L)
            print(1)
        }
        print(2)
    }
}

class AuthorizationService(
    private val authorRepository: IAuthorRepository,
    private val tokenManager: ITokenManager,
    private val emailManager: IEmailManager,
    private val passwordManager: IPasswordManager,
    private val emailVerifyCodeRepository: IEmailVerifyCodeRepository,
) : KoinComponent {
    private val connectionToDb: IConnectionToDb by inject()

    fun signup(request: CreateAuthorRequest): SignupResponse {
        if (!isStrongPassword(request.password))
            return SignupResponse().failed(SignupResponseFailed.WeakPassword)
        if (!isEmailFormatted(request.email))
            return SignupResponse().failed(SignupResponseFailed.InvalidEmailFormat)

        if (authorRepository.getByEmail(request.email) is Author)
            return SignupResponse().failed(SignupResponseFailed.EmailTaken)
        if (authorRepository.getIdByUsername(request.username) is Author)
            return SignupResponse().failed(SignupResponseFailed.UsernameTaken)

        connectionToDb.database.useTransaction {
            val authorId = authorRepository.insertAuthor(request)
            authorId ?: throw ServerErrorException("Failed to create author", this::class.java)
            passwordManager.setNewPassword(request.password, authorId)
            emailManager.welcomeNewAuthor(authorId)
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

    // todo - reset password with current password
    fun resetPassword(currentPassword: String, newPassword: String) {

    }

    fun requestPasswordResetThroughVerifiedEmail(authorId: Int): RequestPasswordResetResponse = runBlocking {
        val email = authorRepository.getById(authorId)?.email
            ?: throw ServerErrorException("author must exist but doesn't.", this::class.java)

        launch {
            emailManager.sendResetPasswordLink(authorId)
        }
        return@runBlocking RequestPasswordResetResponse().succeeded(
            HttpStatusCode.OK,
            PasswordResetResponseData(maskEmail(email))
        )
    }

    // todo - reset password through verified simpleEmail
    fun resetPasswordWithEmailCode(newPassword: String, emailCode: String): ResetPasswordResponse {

        TODO("validate emailCode") // ideally, a confirmation should be sent to mail and the link to reset password

        // get authorId base off of emailCode - I wonder if the simpleEmail getStatusCode is a jwt token??? - It could work
        val authorId = 1

//        return passwordManager.resetPassword(oldPassword, newPassword, authorId)
    }

    private fun login(request: LoginRequest): LoginResponse {
        val author = when (request.loginBy) {
            LoginBy.Email -> authorRepository.getByEmail(request.credential)
                ?: return LoginResponse().failed(LoginResponseFailed.InvalidEmail)
            LoginBy.Username -> authorRepository.getIdByUsername(request.credential)
                ?: return LoginResponse().failed(LoginResponseFailed.InvalidUsername)
        }

        if (!passwordManager.validatePassword(request.password, author.id))
            return LoginResponse().failed(LoginResponseFailed.InvalidPassword)

        val tokenResponse = tokenManager.generateTokens(author.id)
        return LoginResponse().succeeded(HttpStatusCode.OK, tokenResponse)
    }
}