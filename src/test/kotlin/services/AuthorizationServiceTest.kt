package services

import dtos.author.CreateAuthorRequest
import dtos.signup.SignupResultError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.http.*
import io.mockk.*
import managers.interfaces.IEmailManager
import managers.interfaces.IPasswordManager
import managers.interfaces.ITokenManager
import models.profile.Author
import repositories.interfaces.IAuthorRepository
import shared.BehaviorSpecUT

class AuthorizationServiceTest : BehaviorSpecUT({
    val authorRepository: IAuthorRepository = mockk()
    val tokenManager: ITokenManager = mockk()
    val emailService: IEmailManager = mockk()
    val passwordManager: IPasswordManager = mockk()
    val username = "YourNeighborhoodSpider"
    val email = "testemail12345@gmail.com"
    val authorForUsername = Author { val id = 1 }
    val authorForEmail = Author { val id = 2 }

    every { authorRepository.getByUsername(username) } returns null
    every { authorRepository.getByEmail(email) } returns null
    every { emailService.sendResetPassword(authorForUsername.id) }

    val authorizationService = AuthorizationService(authorRepository, tokenManager, emailService, passwordManager)

    fun genCreateAuthorRequest(
        aUsername: String = username,
        aEmail: String = email,
        aPassword: String = "Formidable!76"
    ): CreateAuthorRequest {
        return CreateAuthorRequest(aUsername, aEmail, "Billy", "Bob", aPassword)
    }

    beforeEach {
        println("Hello from $it")
    }

    afterEach {
        println("Goodbye from $it")
    }

    given("signup") {
        And("valid credentials") {
            Then("ok responses") {
                val request = genCreateAuthorRequest()
                every { authorRepository.createAuthor(request) }

                val result = authorizationService.signup(genCreateAuthorRequest())
                result.statusCode shouldBe HttpStatusCode.Created

                verifySequence {
                    authorRepository.createAuthor(request)
                    passwordManager.setNewPassword(request.password)
                    emailService.sendValidateEmail(email)
                }
            }
        }

        And("incorrectly format email") {
            val result = authorizationService.signup(genCreateAuthorRequest(aEmail = "email"))

            result.statusCode shouldBe HttpStatusCode.BadRequest
            result.message shouldBe SignupResultError.getMsg(SignupResultError.EmailTaken)
        }

        And("weak password") {
            val result = authorizationService.signup(genCreateAuthorRequest(aPassword = "password"))

            result.statusCode shouldBe HttpStatusCode.BadRequest
            result.message shouldBe SignupResultError.getMsg(SignupResultError.WeakPassword)
        }

        And("taken email") {
            every { authorRepository.getByEmail(email) } returns authorForEmail

            val result = authorizationService.signup(genCreateAuthorRequest())

            result.statusCode shouldBe HttpStatusCode.BadRequest
            result.message shouldBe SignupResultError.getMsg(SignupResultError.EmailTaken)
        }

        And("taken username") {
            every { authorRepository.getByUsername(username) } returns authorForUsername

            val result = authorizationService.signup(genCreateAuthorRequest())

            result.statusCode shouldBe HttpStatusCode.BadRequest
            result.message shouldBe SignupResultError.getMsg(SignupResultError.UsernameTaken)
        }

        Then("Failed To Create Author server error") {
            TODO("should log the exception")
            val request = genCreateAuthorRequest()
            every { authorRepository.createAuthor(request) } returns 0

            val result = authorizationService.signup(request)

            result.statusCode shouldBe HttpStatusCode.BadRequest
            result.message shouldBe SignupResultError.getMsg(SignupResultError.ServerError)
        }

        Then("Failed To Set New Password server error") {
            TODO("should log the exception")
            val request = genCreateAuthorRequest()
            every { passwordManager.setNewPassword(request.password) } returns 0

            val result = authorizationService.signup(request)

            result.statusCode shouldBe HttpStatusCode.BadRequest
            result.message shouldBe SignupResultError.getMsg(SignupResultError.ServerError)
        }
    }


    given("validateEmailSignupCode") {

    }

    given("login user") {

        And("correct credentials") {
            val result = authorizationService.login("email", "password")
            result.authorId shouldNotBe null
//                result.RefreshToken.length shouldNotBe 0
//                result.accessToken.length shouldNotBe 0
        }

        And("invalid email") {
            val exception = shouldThrow<Exception> {
                authorizationService.login("invalid", "correctPassword")
            }
            exception.message shouldBe "invalid email format"
        }

        And("password provided is invalid") {
            var exception = shouldThrow<Exception> {
                authorizationService.login("correctEmail", "invalid")
            }
            exception.message shouldBe "invalid password format"
        }

    }

    given("refreshAccessToken") { }

    given("requestPasswordReset") {

        And("valid username and email") {
            val result = authorizationService.requestPasswordReset(username, email)
        }

    }

    given("setNewPasswordForSignup") { }

    given("resetPasswordByEmail") { }
})
