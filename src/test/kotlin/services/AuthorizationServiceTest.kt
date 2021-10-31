package services

import dtos.author.CreateAuthorRequest
import dtos.login.LoginByEmailRequest
import dtos.signup.SignupResponseFailed
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verifySequence
import managers.interfaces.IEmailManager
import managers.interfaces.IPasswordManager
import managers.interfaces.ITokenManager
import models.profile.Author
import repositories.interfaces.IAuthorRepository
import repositories.interfaces.IEmailVerifyCodeRepository
import shared.BehaviorSpecUT

class AuthorizationServiceTest : BehaviorSpecUT({
    val authorRepository: IAuthorRepository = mockk()
    val tokenManager: ITokenManager = mockk()
    val emailManager: IEmailManager = mockk()
    val passwordManager: IPasswordManager = mockk()
    val emailVerifyCodeRepository: IEmailVerifyCodeRepository = mockk()

    val username = "YourNeighborhoodSpider"
    val email = "testemail12345@gmail.com"
    val password = "Formidable!76"
    val authorForUsername = Author { val id = 1 }
    val authorForEmail = Author { val id = 2 }

    every { authorRepository.getByUsername(username) } returns null
    every { authorRepository.getByEmail(email) } returns null
    justRun { emailManager.sendValidateEmail(email) }
    every { passwordManager.setNewPassword(password) } returns 32

    val authorizationService =
        AuthorizationService(authorRepository, tokenManager, emailManager, passwordManager, emailVerifyCodeRepository)

    given("signup") {
        fun genCreateAuthorRequest(
            aUsername: String = username, aEmail: String = email, aPassword: String = password,
        ): CreateAuthorRequest {
            return CreateAuthorRequest(aUsername, aEmail, "Billy", "Bob", aPassword)
        }
        and("valid credentials") {
            then("respond with Created") {
                val request = genCreateAuthorRequest()
                every { authorRepository.createAuthor(request) } returns 123 // TODO replace ktorm with exposed and change return type to bool

                val result = authorizationService.signup(genCreateAuthorRequest())

                verifySequence {
                    authorRepository.getByEmail(request.email)
                    authorRepository.getByUsername(username)
                    authorRepository.createAuthor(request)
                    passwordManager.setNewPassword(request.password)
                    emailManager.sendValidateEmail(email)
                }

                result.statusCode() shouldBe HttpStatusCode.Created
            }
        }

        and("weak password") {
            val result = authorizationService.signup(genCreateAuthorRequest(aPassword = "password"))

            result.statusCode() shouldBe HttpStatusCode.BadRequest
            result.message() shouldBe SignupResponseFailed.getMsg(SignupResponseFailed.WeakPassword)
        }

        and("incorrectly format email") {
            val result = authorizationService.signup(genCreateAuthorRequest(aEmail = "email"))

            result.statusCode() shouldBe HttpStatusCode.BadRequest
            result.message() shouldBe SignupResponseFailed.getMsg(SignupResponseFailed.InvalidEmailFormat)
        }

        and("taken email") {
            every { authorRepository.getByEmail(email) } returns authorForEmail

            val result = authorizationService.signup(genCreateAuthorRequest())

            result.statusCode() shouldBe HttpStatusCode.BadRequest
            result.message() shouldBe SignupResponseFailed.getMsg(SignupResponseFailed.EmailTaken)
        }

        and("taken email") {
            every { authorRepository.getByUsername(username) } returns authorForUsername

            val result = authorizationService.signup(genCreateAuthorRequest())

            result.statusCode() shouldBe HttpStatusCode.BadRequest
            result.message() shouldBe SignupResponseFailed.getMsg(SignupResponseFailed.UsernameTaken)
        }

    }

    given("validateEmailSignupCode") {

    }

    given("login") {

        and("login through email") {
            then("return tokens") {
                val result = authorizationService.login(LoginByEmailRequest(email = email, password = password))

//                result.data().accessToken.length.shouldBeGreaterThan(0)
//                result.data().refreshToken.length.shouldBeGreaterThan(0)
            }
        }

        and("invalid email") {
            then("return 400") {
                val res = authorizationService.login(LoginByEmailRequest(email = email, password = password))
                res.message() shouldBe "invalid email format"
            }
        }

        And("invalid password") {
            then("return 400") {
                val res = authorizationService.login(LoginByEmailRequest(email = email, password = password))
                res.message() shouldBe "invalid password format"
            }
        }

        And("invalid username") {

        }
    }

    given("refreshAccessToken") { }

    given("requestPasswordReset") {

        And("valid email and email") {
            val result = authorizationService.requestPasswordReset(username, email)
        }

    }

    given("setNewPasswordForSignup") { }

    given("resetPasswordByEmail") { }
})
