package services

import configurations.AppEnv
import dtos.authorization.*
import dtos.failed
import dtos.login.LoginBy
import dtos.login.LoginRequest
import dtos.responseData.PasswordResetResponseData
import dtos.signup.SignupResponse
import dtos.signup.SignupResponseFailed
import dtos.succeeded
import exceptions.ServerErrorException
import helper.JwtHelper
import helper.isEmailFormatted
import helper.isStrongPassword
import helper.maskEmail
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import managers.EmailManager
import managers.PasswordManager
import managers.TokenManager
import models.profile.Author
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repositories.AuthorRepository
import repositories.codes.EmailVerificationCodeRepository
import serialized.CreateAuthorRequest
import serialized.LoginByEmailRequest
import serialized.LoginByUsernameRequest

fun main() {
    runBlocking {
        launch {
            delay(2000L)
            print(1)
        }
        print(2)
    }
}

class AuthorizationService(
    private val tokenManager: TokenManager,
    private val emailManager: EmailManager,
    private val passwordManager: PasswordManager,
    private val authorRepository: AuthorRepository,
    private val emailVerificationCodeRepository: EmailVerificationCodeRepository,
) : KoinComponent {
    val appEnv: AppEnv by inject()

    fun signup(request: CreateAuthorRequest): SignupResponse {
        if (!isStrongPassword(request.password))
            return SignupResponse().failed(SignupResponseFailed.WeakPassword)
        if (!isEmailFormatted(request.email))
            return SignupResponse().failed(SignupResponseFailed.InvalidEmailFormat)

        if (authorRepository.getByEmail(request.email) is Author)
            return SignupResponse().failed(SignupResponseFailed.EmailTaken)
        if (authorRepository.getByUsername(request.username) is Author)
            return SignupResponse().failed(SignupResponseFailed.UsernameTaken)

        appEnv.database.useTransaction {
            val authorId = authorRepository.insert(request)
            authorId ?: throw ServerErrorException("Failed to create author", this::class.java)
            passwordManager.setNewPassword(request.password, authorId)
            emailManager.welcomeNewAuthor(authorId)
            val tokens = tokenManager.generateAuthTokens(authorId)

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

    private fun login(request: LoginRequest): LoginResponse {
        val author = when (request.loginBy) {
            LoginBy.Email -> authorRepository.getByEmail(request.credential)
                ?: return LoginResponse().failed(LoginResponseFailed.InvalidEmail)
            LoginBy.Username -> authorRepository.getByUsername(request.credential)
                ?: return LoginResponse().failed(LoginResponseFailed.InvalidUsername)
        }

        if (!passwordManager.validatePassword(request.password, author.id))
            return LoginResponse().failed(LoginResponseFailed.InvalidPassword)

        val tokenResponse = tokenManager.generateAuthTokens(author.id)
        return LoginResponse().succeeded(HttpStatusCode.OK, tokenResponse)
    }

    fun refreshAccessToken(refreshToken: String, authorId: Int): TokensResponse {
        return tokenManager.refreshAccessToken(refreshToken, authorId)
    }

    // todo
    fun verifyEmail(request: VerifyEmailCodeRequest): VerifyEmailCodeResponse {
        val authorId = 2 // todo get authorId from token header

        val codeDb = emailVerificationCodeRepository.getCode(authorId)
            ?: return VerifyEmailCodeResponse().failed(VerifyEmailCodeResponseFailed.DoesNotExistEmailCode)

        return if (codeDb == request.code)
            VerifyEmailCodeResponse().succeeded(HttpStatusCode.OK)
        else VerifyEmailCodeResponse().failed(VerifyEmailCodeResponseFailed.InvalidEmailCode)
    }

    // todo -
    //  email verification,
    //  send reset password code to email,
    //  reset password with email code,
    //  reset password with current password

    fun resetPassword(currentPassword: String, newPassword: String, authorId: Int): ResetPasswordResponse {
        if (!isStrongPassword(newPassword))
            return ResetPasswordResponse().failed(ResetPasswordResponseFailed.WeakPassword)

        return passwordManager.changePassword(currentPassword, newPassword, authorId)
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

    // todo - reset password through verified email
    fun resetPasswordThroughEmail(newPassword: String, emailCode: String): ResetPasswordThroughEmailResponse {
        // todo - verify that email in file is verified

//        TODO("validate emailCode") // ideally, a confirmation should be sent to mail and the link to reset password

        val authorId = JwtHelper.verifyAndGetAuthorId(emailCode, appEnv.getConfig("jwt.secret"))
            ?: return ResetPasswordThroughEmailResponse().failed(ResetPasswordThroughEmailResponseFailed.InvalidEmailCode)

        appEnv.database.useTransaction {
            if (!emailVerificationCodeRepository.delete(authorId))
                return ResetPasswordThroughEmailResponse().failed(ResetPasswordThroughEmailResponseFailed.EmailCodeNoInOurRecords)

            passwordManager.setNewPassword(newPassword, authorId)
        }

        return ResetPasswordThroughEmailResponse().succeeded(HttpStatusCode.Created)

        // get authorId base off of emailCode - I wonder if the email getStatusCode is a jwt token??? - It could work
//        val authorId = 1

//        return passwordManager.changePassword(oldPassword, newPassword, authorId)
    }

}