package services

import dtos.authorization.LoginResponse
import dtos.authorization.LoginResponseFailed
import dtos.authorization.TokensResponse
import dtos.signup.SignupResponseFailed
import dtos.token.responseData.ITokenResponseData
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.mockk.*
import managers.interfaces.IEmailManager
import managers.interfaces.IPasswordManager
import managers.interfaces.ITokenManager
import models.profile.Author
import repositories.interfaces.IAuthorRepository
import repositories.interfaces.IEmailVerifyCodeRepository
import serialized.CreateAuthorRequest
import serialized.LoginByEmailRequest
import serialized.LoginByUsernameRequest
import serialized.TokenResponseData
import shared.testUtils.BehaviorSpecUT

class AuthorizationServiceUnitTest : BehaviorSpecUT({
    val authorRepository: IAuthorRepository = mockk()
    val tokenManager: ITokenManager = mockk()
    val emailManager: IEmailManager = mockk()
    val passwordManager: IPasswordManager = mockk()
    val emailVerifyCodeRepository: IEmailVerifyCodeRepository = mockk()
    val author: Author = mockk()
    val username = "YourNeighborhoodSpider"
    val email = "testemail12345@gmail.com"
    val password = "Formidable!76"
    val authorId = 3
    val tokenResponseData = TokenResponseData("access token", "refresh token")

    val authorizationService =
        AuthorizationService(authorRepository, tokenManager, emailManager, passwordManager, emailVerifyCodeRepository)

    fun validateTokens(data: ITokenResponseData) {
        data.refreshToken shouldBe tokenResponseData.refreshToken
        data.accessToken shouldBe tokenResponseData.accessToken
    }

    beforeEach {
        clearAllMocks()

        every { author.id } returns authorId
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
            every { authorRepository.getByEmail(email) } returns author

            val result = authorizationService.signup(genCreateAuthorRequest())

            result.statusCode() shouldBe HttpStatusCode.BadRequest
            result.message() shouldBe SignupResponseFailed.getMsg(SignupResponseFailed.EmailTaken)
        }
        then("taken username") {
            every { authorRepository.getByEmail(email) } returns null
            every { authorRepository.getByUsername(username) } returns author

            val result = authorizationService.signup(genCreateAuthorRequest())

            result.statusCode() shouldBe HttpStatusCode.BadRequest
            result.message() shouldBe SignupResponseFailed.getMsg(SignupResponseFailed.UsernameTaken)
        }
        then("provided with valid credentials") {
            val request = genCreateAuthorRequest()

            every { authorRepository.getByUsername(request.username) } returns null
            every { authorRepository.getByEmail(request.email) } returns null
            justRun { emailManager.welcomeNewAuthor(request.email) }
            every { passwordManager.setNewPassword(request.password, authorId = authorId) } returns true
            every { tokenManager.generateTokens(authorId = authorId) } returns tokenResponseData

            every { authorRepository.insertAuthor(request) } returns authorId

            val result = authorizationService.signup(request)

            verifySequence {
                authorRepository.getByEmail(request.email)
                authorRepository.getByUsername(request.username)
                authorRepository.insertAuthor(request)
                passwordManager.setNewPassword(request.password, authorId)
                emailManager.welcomeNewAuthor(request.email)
            }

            result.statusCode() shouldBe HttpStatusCode.Created
            validateTokens(result.data!!)
        }
    }

    xgiven("validateEmailSignupCode") {

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
                every { authorRepository.getByEmail(requestByEmail.email) } returns null

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

    xgiven("requestPasswordReset") {
        then("valid email and email") {
            val res = authorizationService.requestPasswordReset(username, email)


//            res.statusCode()
        }
    }

    xgiven("setNewPasswordForSignup") { }

    xgiven("resetPasswordByEmail") { }
})
