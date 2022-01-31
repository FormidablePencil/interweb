package com.idealIntent.services

import com.idealIntent.configurations.AppEnv
import com.idealIntent.dtos.CreateAuthorRequest
import com.idealIntent.dtos.auth.LoginByEmailRequest
import com.idealIntent.dtos.auth.LoginByUsernameRequest
import com.idealIntent.dtos.auth.TokenResponseData
import com.idealIntent.helpers.maskEmail
import com.idealIntent.managers.EmailManager
import com.idealIntent.managers.PasswordManager
import com.idealIntent.managers.TokenManager
import com.idealIntent.repositories.codes.EmailVerificationCodeRepository
import com.idealIntent.repositories.profile.AccountRepository
import com.idealIntent.repositories.profile.AuthorProfileRelatedRepository
import com.idealIntent.repositories.profile.AuthorRepository
import dtos.authorization.LoginResponse
import dtos.authorization.LoginResponseFailed
import dtos.authorization.ResetPasswordResponse
import dtos.authorization.TokensResponse
import dtos.responseData.ITokenResponseData
import dtos.signup.SignupResponseFailed
import dtos.succeeded
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.mockk.*
import models.profile.IAccountEntity
import models.profile.IAuthorEntity
import shared.appEnvMockHelper

class AuthorizationServiceTest : BehaviorSpec({
    val authorRepository: AuthorRepository = mockk()
    val tokenManager: TokenManager = mockk()
    val emailManager: EmailManager = mockk()
    val passwordManager: PasswordManager = mockk()
    val emailVerifyCodeRepository: EmailVerificationCodeRepository = mockk()
    val authorProfileRelatedRepository: AuthorProfileRelatedRepository = mockk()
    val accountRepository: AccountRepository = mockk()
    val author: IAuthorEntity = mockk()
    val account: IAccountEntity = mockk()
    val username = "YourNeighborhoodSpider"
    val email = "testemail12345@gmail.com"
    val password = "Formidable!76"
    val authorId = 3
    val tokenResponseData = TokenResponseData("access token", "refresh token")
    val appEnv = mockk<AppEnv>()

    val authorizationService = spyk(
        AuthorizationService(
            tokenManager,
            emailManager,
            passwordManager,
            authorRepository,
            accountRepository,
            authorProfileRelatedRepository,
            emailVerifyCodeRepository,
        )
    )

    fun validateTokens(data: ITokenResponseData) {
        data.refreshToken shouldBe tokenResponseData.refreshToken
        data.accessToken shouldBe tokenResponseData.accessToken
    }

    beforeEach {
        clearAllMocks()

        appEnvMockHelper(appEnv, authorizationService)

        every { author.id } returns authorId
        every { account.email } returns email
    }

    given("signup") {
        fun genCreateAuthorRequest(
            aUsername: String = username, aEmail: String = email, aPassword: String = password
        ): CreateAuthorRequest {
            return CreateAuthorRequest(
                firstname = "Billy",
                lastname = "Bob",
                username = aUsername,
                email = aEmail,
                password = aPassword
            )
        }
        then("provided with weak password") {
            val result = authorizationService.signup(genCreateAuthorRequest(aPassword = "password"))

            result.statusCode() shouldBe HttpStatusCode.BadRequest
            result.message() shouldBe SignupResponseFailed.getMsg(SignupResponseFailed.WeakPassword)
        }
        then("incorrectly format email") {
            val result = authorizationService.signup(genCreateAuthorRequest(aEmail = "email"))

            result.statusCode() shouldBe HttpStatusCode.BadRequest
            result.message() shouldBe SignupResponseFailed.getMsg(SignupResponseFailed.InvalidEmailFormat)
        }
        then("taken email") {
            every { accountRepository.getByEmail(email) } returns account

            val result = authorizationService.signup(genCreateAuthorRequest())

            result.statusCode() shouldBe HttpStatusCode.BadRequest
            result.message() shouldBe SignupResponseFailed.getMsg(SignupResponseFailed.EmailTaken)
        }
        then("taken username") {
            every { accountRepository.getByEmail(email) } returns null
            every { authorRepository.getByUsername(username) } returns author

            val result = authorizationService.signup(genCreateAuthorRequest())

            result.statusCode() shouldBe HttpStatusCode.BadRequest
            result.message() shouldBe SignupResponseFailed.getMsg(SignupResponseFailed.UsernameTaken)
        }
        then("provided with valid credentials") {
            val request = genCreateAuthorRequest()

            every { authorRepository.getByUsername(request.username) } returns null
            every { accountRepository.getByEmail(request.email) } returns null
            justRun { emailManager.welcomeNewAuthor(authorId) }
            justRun { passwordManager.setNewPassword(request.password, authorId = authorId) }
            every { tokenManager.generateAuthTokens(authorId = authorId) } returns tokenResponseData

            every { authorProfileRelatedRepository.createNewAuthor(request) } returns authorId

            val result = authorizationService.signup(request)

            verifySequence {
                accountRepository.getByEmail(request.email)
                authorRepository.getByUsername(request.username)
                authorProfileRelatedRepository.createNewAuthor(request)
                passwordManager.setNewPassword(request.password, authorId)
                emailManager.welcomeNewAuthor(authorId)
            }

            result.statusCode() shouldBe HttpStatusCode.Created
            validateTokens(result.data!!)
        }
    }

    // todo - increment amount of login attempts to lock account
    given("login") {
        val requestByEmail = LoginByEmailRequest(email, password)
        val requestByUsername = LoginByUsernameRequest(username, password)

        fun login(isLoginByEmail: Boolean): LoginResponse {
            return if (isLoginByEmail)
                authorizationService.login(requestByEmail)
            else
                authorizationService.login(requestByUsername)
        }
        and("invalid identification (email or username)") {
            fun invalidIdentificationValidation(result: LoginResponse) {
                result.message() shouldBe LoginResponseFailed.getMsg(LoginResponseFailed.InvalidEmail)
                result.statusCode() shouldBe HttpStatusCode.BadRequest
            }
            then("when attempting to login by email") {
                every { accountRepository.getByEmail(requestByEmail.email) } returns null

                invalidIdentificationValidation(login(true))
            }
            then("when attempting to login by username") {
                every { authorRepository.getByUsername(requestByUsername.username) } returns null

                invalidIdentificationValidation(login(false))
            }
        }
        then("invalid password") {
            every { authorRepository.getByUsername(requestByUsername.username) } returns author
            every { passwordManager.validatePassword(requestByUsername.password, authorId) } returns false

            val result = login(false)

            result.message() shouldBe LoginResponseFailed.getMsg(LoginResponseFailed.InvalidPassword)
            result.statusCode() shouldBe HttpStatusCode.BadRequest
        }
        then("valid credentials") {
            val expectedResponse = TokenResponseData("refresh token", "access token")

            every { authorRepository.getByUsername(requestByUsername.username) } returns author
            every { passwordManager.validatePassword(requestByUsername.password, authorId) } returns true
            every { tokenManager.generateAuthTokens(authorId) } returns expectedResponse

            val res = login(false)

            val data = res.data ?: throw Exception()
            data.refreshToken shouldBe expectedResponse.refreshToken
            data.accessToken shouldBe expectedResponse.accessToken
        }
    }

    given("refreshAccessToken") {
        then("provided valid refresh token") {
            val tokenResponse = TokensResponse()
            every { tokenManager.refreshAccessToken(tokenResponseData.refreshToken, authorId) } returns tokenResponse

            val res = authorizationService.refreshAccessToken(tokenResponseData.refreshToken, authorId)

            res shouldBe tokenResponse
            verify(exactly = 1) { tokenManager.refreshAccessToken(tokenResponseData.refreshToken, authorId) }
        }
    }

    xgiven("verifyEmail") {

    }

    given("changePassword") {
        then("with new weak password") {
            val res = authorizationService.resetPassword("Current!123", "weakPass", authorId)
            res.statusCode() shouldBe HttpStatusCode.BadRequest
        }
        then("with strong password") {
            val currentPassword = "MyCurrentPassword!123"
            val newPassword = "MyNewPassword!123"

            val resetPasswordResponse = ResetPasswordResponse().succeeded(HttpStatusCode.OK, tokenResponseData)
            every {
                passwordManager.changePassword(
                    currentPassword,
                    newPassword,
                    authorId
                )
            } returns resetPasswordResponse

            val res = authorizationService.resetPassword(currentPassword, newPassword, authorId)

            res.statusCode() shouldBe HttpStatusCode.OK
            res.data!!.refreshToken.length shouldBeGreaterThan 0
            res.data!!.accessToken.length shouldBeGreaterThan 0
        }
    }

    given("requestPasswordResetThroughVerifiedEmail") {
        then("should send reset password link to email") {
            every { accountRepository.getById(authorId) } returns account
            coJustRun { emailManager.sendResetPasswordLink(authorId) }

            val res = authorizationService.requestPasswordResetThroughVerifiedEmail(authorId)

            res.statusCode() shouldBe HttpStatusCode.OK
            res.data!!.maskedEmail shouldBe maskEmail(email)
        }
    }

    xgiven("resetPasswordWithThoughEmail") { }
})