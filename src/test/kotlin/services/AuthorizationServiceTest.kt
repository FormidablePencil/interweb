package services

import configurations.AppEnv
import dtos.authorization.LoginResponse
import dtos.authorization.LoginResponseFailed
import dtos.authorization.TokensResponse
import dtos.signup.SignupResponseFailed
import dtos.token.responseData.ITokenResponseData
import helper.maskEmail
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.mockk.*
import managers.EmailManager
import managers.PasswordManager
import managers.TokenManager
import models.profile.Author
import repositories.AuthorRepository
import repositories.codes.EmailVerificationCodeRepository
import serialized.CreateAuthorRequest
import serialized.LoginByEmailRequest
import serialized.LoginByUsernameRequest
import serialized.TokenResponseData
import shared.appEnvMockHelper

class AuthorizationServiceTest : BehaviorSpec({
    val authorRepository: AuthorRepository = mockk()
    val tokenManager: TokenManager = mockk()
    val emailManager: EmailManager = mockk()
    val passwordManager: PasswordManager = mockk()
    val emailVerifyCodeRepository: EmailVerificationCodeRepository = mockk()
    val author: Author = mockk()
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
        every { author.email } returns email
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
        then("incorrectly format simpleEmail") {
            val result = authorizationService.signup(genCreateAuthorRequest(aEmail = "simpleEmail"))

            result.statusCode() shouldBe HttpStatusCode.BadRequest
            result.message() shouldBe SignupResponseFailed.getMsg(SignupResponseFailed.InvalidEmailFormat)
        }
        then("taken simpleEmail") {
            every { authorRepository.getByEmail(email) } returns author

            val result = authorizationService.signup(genCreateAuthorRequest())

            result.statusCode() shouldBe HttpStatusCode.BadRequest
            result.message() shouldBe SignupResponseFailed.getMsg(SignupResponseFailed.EmailTaken)
        }
        then("taken username") {
            every { authorRepository.getByEmail(email) } returns null
            every { authorRepository.getIdByUsername(username) } returns author

            val result = authorizationService.signup(genCreateAuthorRequest())

            result.statusCode() shouldBe HttpStatusCode.BadRequest
            result.message() shouldBe SignupResponseFailed.getMsg(SignupResponseFailed.UsernameTaken)
        }
        then("provided with valid credentials") {
            val request = genCreateAuthorRequest()

            every { authorRepository.getIdByUsername(request.username) } returns null
            every { authorRepository.getByEmail(request.email) } returns null
            justRun { emailManager.welcomeNewAuthor(authorId) }
            every { passwordManager.setNewPassword(request.password, authorId = authorId) } returns true
            every { tokenManager.generateTokens(authorId = authorId) } returns tokenResponseData

            every { authorRepository.insertAuthor(request) } returns authorId

            val result = authorizationService.signup(request)

            verifySequence {
                authorRepository.getByEmail(request.email)
                authorRepository.getIdByUsername(request.username)
                authorRepository.insertAuthor(request)
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
        and("invalid identification (simpleEmail or username)") {
            fun invalidIdentificationValidation(result: LoginResponse) {
                result.message() shouldBe LoginResponseFailed.getMsg(LoginResponseFailed.InvalidEmail)
                result.statusCode() shouldBe HttpStatusCode.BadRequest
            }
            then("when attempting to login by simpleEmail") {
                every { authorRepository.getByEmail(requestByEmail.email) } returns null

                invalidIdentificationValidation(login(true))
            }
            then("when attempting to login by username") {
                every { authorRepository.getIdByUsername(requestByUsername.username) } returns null

                invalidIdentificationValidation(login(false))
            }
        }
        then("invalid password") {
            every { authorRepository.getIdByUsername(requestByUsername.username) } returns author
            every { passwordManager.validatePassword(requestByUsername.password, authorId) } returns false

            val result = login(false)

            result.message() shouldBe LoginResponseFailed.getMsg(LoginResponseFailed.InvalidPassword)
            result.statusCode() shouldBe HttpStatusCode.BadRequest
        }
        then("valid credentials") {
            val expectedResponse = TokenResponseData("refresh token", "access token")

            every { authorRepository.getIdByUsername(requestByUsername.username) } returns author
            every { passwordManager.validatePassword(requestByUsername.password, authorId) } returns true
            every { tokenManager.generateTokens(authorId) } returns expectedResponse

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

    xgiven("resetPassword") { }

    given("requestPasswordResetThroughVerifiedEmail") {
        then("should send reset password link to simpleEmail") {
            every { authorRepository.getById(authorId) } returns author
            coJustRun { emailManager.sendResetPasswordLink(authorId) }

            val res = authorizationService.requestPasswordResetThroughVerifiedEmail(authorId)

            res.statusCode() shouldBe HttpStatusCode.OK
            res.data!!.maskedEmail shouldBe maskEmail(email)
        }
    }

    xgiven("resetPasswordWithThoughEmail") { }
})