package com.idealIntent.services

import com.idealIntent.configurations.AppEnv
import dtos.authorization.*
import dtos.failed
import dtos.login.LoginBy
import dtos.login.LoginRequest
import dtos.responseData.PasswordResetResponseData
import dtos.signup.SignupResponse
import dtos.signup.SignupResponseFailed
import dtos.succeeded
import com.idealIntent.exceptions.ServerErrorException
import com.idealIntent.helper.JwtHelper
import com.idealIntent.helper.isEmailFormatted
import com.idealIntent.helper.isStrongPassword
import com.idealIntent.helper.maskEmail
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import com.idealIntent.managers.EmailManager
import com.idealIntent.managers.PasswordManager
import com.idealIntent.managers.TokenManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.idealIntent.repositories.codes.EmailVerificationCodeRepository
import com.idealIntent.repositories.profile.AccountRepository
import com.idealIntent.repositories.profile.AuthorProfileRelatedRepository
import com.idealIntent.repositories.profile.AuthorRepository
import com.idealIntent.serialized.CreateAuthorRequest
import com.idealIntent.serialized.auth.LoginByEmailRequest
import com.idealIntent.serialized.auth.LoginByUsernameRequest

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
    private val accountRepository: AccountRepository,
    private val authorProfileRelatedRepository: AuthorProfileRelatedRepository,
    private val emailVerificationCodeRepository: EmailVerificationCodeRepository,
) : KoinComponent {
    val appEnv: AppEnv by inject()

    fun signup(request: CreateAuthorRequest): SignupResponse {
        val validateSignupRequestResult = validateSignupRequest(request)
        if (validateSignupRequestResult != null) return validateSignupRequestResult

        appEnv.database.useTransaction {
            val authorId = authorProfileRelatedRepository.createNewAuthor(request)
            authorId ?: throw ServerErrorException("Failed to create author", this::class.java)
            passwordManager.setNewPassword(request.password, authorId)
            emailManager.welcomeNewAuthor(authorId)
            val tokens = tokenManager.generateAuthTokens(authorId)

            return SignupResponse().succeeded(HttpStatusCode.Created, tokens)
        }
    }

    private fun validateSignupRequest(request: CreateAuthorRequest): SignupResponse? {
        if (!isStrongPassword(request.password))
            return SignupResponse().failed(SignupResponseFailed.WeakPassword)
        if (!isEmailFormatted(request.email))
            return SignupResponse().failed(SignupResponseFailed.InvalidEmailFormat)

        if (accountRepository.getByEmail(request.email) != null)
            return SignupResponse().failed(SignupResponseFailed.EmailTaken)
        if (authorRepository.getByUsername(request.username) != null)
            return SignupResponse().failed(SignupResponseFailed.UsernameTaken)

        return null
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
        val authorId = when (request.loginBy) {
            LoginBy.Email -> accountRepository.getByEmail(request.credential)?.authorId
                ?: return LoginResponse().failed(LoginResponseFailed.InvalidEmail)
            LoginBy.Username -> authorRepository.getByUsername(request.credential)?.id
                ?: return LoginResponse().failed(LoginResponseFailed.InvalidUsername)
        }

        if (!passwordManager.validatePassword(request.password, authorId))
            return LoginResponse().failed(LoginResponseFailed.InvalidPassword)

        val tokenResponse = tokenManager.generateAuthTokens(authorId)
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
        val email = accountRepository.getById(authorId)?.email
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