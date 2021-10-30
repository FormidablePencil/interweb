package services

import configurations.interfaces.IConnectionToDb
import dtos.author.CreateAuthorRequest
import dtos.authorization.*
import dtos.failed
import dtos.login.*
import dtos.signup.SignupResponse
import dtos.signup.SignupResponseFailed
import dtos.succeeded
import exceptions.ServerFailed
import exceptions.ServerErrorException
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

        if (authorRepository.getByEmail(request.email) != null)
            return SignupResponse().failed(SignupResponseFailed.EmailTaken)
        if (authorRepository.getByUsername(request.username) != null)
            return SignupResponse().failed(SignupResponseFailed.UsernameTaken)

        connectionToDb.database.useTransaction {
            if (authorRepository.createAuthor(request) == 0)
                throw ServerErrorException(ServerFailed.FailedToCreateAuthor, this::class.java)
            if (passwordManager.setNewPassword(request.password) == 0)
                throw ServerErrorException(ServerFailed.FailedToSetNewPassword, this::class.java)
            emailManager.sendValidateEmail(request.email)

            return SignupResponse().succeeded(HttpStatusCode.Created)
        }
    }

    fun verifyEmailCodeAndLogin(request: VerifyEmailCodeAndLoginByUsernameRequest): LoginResponse {
        // have the user enter their new credentials on the page where the url has emailVerificationCode
        // this is the only gateway of signing unit done it for the first time cause code validation will be done after valid credentials provided
        val authorId = 1 // todo get authorId from token
        var loginResult: TokensResponse
        val verifyEmailResult = verifyEmailCode(request.code)

        if (verifyEmailResult.success) {
            when (verifyEmailResult.failedCode) {
                VerifyEmailCodeResultFailed.InvalidEmailCode -> return LoginResponse()
                VerifyEmailCodeResultFailed.DoesNotExistEmailCode ->
                    throw ServerErrorException(ServerFailed.DoesNotExistEmailCode, this::class.java)
            }
        }

        return login(LoginReq(credential = request.username, password = request.password, loginBy = LoginBy.Username))
//        return loginResult
    }

    fun login(request: LoginByEmailRequest): LoginResponse {
        return login(LoginReq(credential = request.email, password = request.password, loginBy = LoginBy.Email))
    }

    fun login(request: LoginByUsernameRequest): LoginResponse {
        return login(LoginReq(credential = request.username, password = request.password, loginBy = LoginBy.Username))
    }

    fun refreshAccessToken(refreshToken: String, authorId: Int): TokensResponse {
        return tokenManager.refreshAccessToken(refreshToken, authorId)
    }

    fun requestPasswordReset(username: String?, email: String?): RequestPasswordResetResponse {
        val author: Author?

        if (!username.isNullOrEmpty()) {
            author = authorRepository.getByUsername(username)
            if (author == null) return RequestPasswordResetResponse().failed(RequestPasswordResetResponseFailed.AccountNotFoundByGivenUsername)
        } else if (!email.isNullOrEmpty()) {
            author = authorRepository.getByEmail(email)
            if (author == null) return RequestPasswordResetResponse().failed(RequestPasswordResetResponseFailed.AccountNotFoundByGivenEmail)
        } else
            return RequestPasswordResetResponse().failed(RequestPasswordResetResponseFailed.NeitherUsernameNorEmailProvided)

        emailManager.sendValidateEmail(author.email)

        val maskedEmail = maskEmail(author.email)
        return RequestPasswordResetResponse(maskedEmail).succeeded()
    }

    fun resetPasswordByEmail(oldPassword: String, newPassword: String, emailCode: String): ResetPasswordResponse {

        TODO("validate emailCode") // ideally, a confirmation should be sent to mail and the link to reset password

        // get authorId base off of emailCode - I wonder if the email statusCode is a jwt token??? - It could work
        val authorId = 1

        return passwordManager.resetPassword(oldPassword, newPassword, authorId)
    }

    private fun login(request: LoginReq): LoginResponse {
        // TODO check if first time email verification done
        val author: Author? = authorRepository.getByUsername(request.username)
        if (author?.id == null)
            return LoginResponse().failed(LoginResponseFailed.InvalidUsername)
        if (validatePassword(request.password, author.id))
        val tokenResponse = tokenManager.generateTokens(author.id)
        return LoginResponse(authorId = author)
// TODO       return tokens
    }

    private fun validatePassword(password: String, authorId: Int): LoginResponse {
        // route needs to respond with invalid credentials response
        return if (passwordManager.validatePassword(password, authorId))
            LoginResponse(authorId).succeeded()
        else LoginResponse().failed(LoginResponseFailed.InvalidPassword)
    }

    private fun verifyEmailCode(code: String): VerifyEmailCodeResult {
        val authorId = 2 // todo get authorId from token header

        val codeDb = emailVerifyCodeRepository.get(authorId)
            ?: return VerifyEmailCodeResult().failed(VerifyEmailCodeResultFailed.DoesNotExistEmailCode)

        return if (codeDb == code)
            VerifyEmailCodeResult().succeeded()
        else VerifyEmailCodeResult().failed(VerifyEmailCodeResultFailed.InvalidEmailCode)
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