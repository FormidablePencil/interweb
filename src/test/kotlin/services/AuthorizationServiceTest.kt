package services

import dtos.author.CreateAuthorRequest
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import managers.interfaces.IEmailManager
import managers.interfaces.IPasswordManager
import managers.interfaces.ITokenManager
import models.profile.Author
import repositories.interfaces.IAuthorRepository
import shared.DataTestName
import shared.PythagTriple3

fun isPythagTriple(a: Int, b: Int, c: Int): Boolean = a * a + b * b == c * c

@OptIn(ExperimentalKotest::class)
class AuthorizationServiceTest : FunSpec({
    val authorRepository: IAuthorRepository = mockk()
    val tokenManager: ITokenManager = mockk()
    val emailService: IEmailManager = mockk()
    val passwordManager: IPasswordManager = mockk()
    val username = "YourNeighborhoodSpider"
    val email = "testemail12345@gmail.com"
    val authorForUsername = Author { val id = 1 }
    val authorForEmail = Author { val id = 2 }

    every { authorRepository.getByUsername(username) } returns authorForUsername
    every { authorRepository.getByUsername(email) } returns authorForEmail
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

    context("test again 1") {
        withData(
            PythagTriple3(6, 8, 10)
                .set("strong password", false)
                .set("email formatted", false),
            PythagTriple3(3, 4, 5)
                .set("strong password", true)
                .set("email formatted", false),
            PythagTriple3(3, 4, 5)
                .set("strong password", false)
                .set("email formatted", true),
            PythagTriple3(6, 8, 10)
                .set("strong password", true)
                .set("email formatted", true),
        ) { (a, b, c) ->
            isPythagTriple(a, b, c) shouldBe true
        }
    }

//    context("login 2") {
//        test("variations") {
//            withData(
//                PythagTriple(3, 4, 5), // incorrect email or password for instance
//                PythagTriple(6, 8, 10),
//                PythagTriple(8, 15, 17),
//                PythagTriple(7, 24, 25)
//            ) { (a, b, c) ->
//
//                when (a) {
//                    3, 8 -> { // if valid email or username for instance
//                        println("one")
//                        println("two")
//                        isPythagTriple(a, b, c) shouldBe false
//                    }
//                    6 -> {
//                        println("twenty five")
//                        isPythagTriple(a, b, c) shouldBe true
//                    }
//                    else -> {
//                        println("fifty nine")
//                        isPythagTriple(a, b, c) shouldBe true
//                    }
//                }
//            }
//        }
//    }


//    given("signup new user") {
//        println("should print once because it should run only once")
//
//        Then("valid credentials") {
//
////                val result = authorizationService.signup(genCreateAuthorRequest())
//
//            4 shouldBe 4
//            3 shouldBe 3
//
//            //region assertions
////                result.authorId shouldBe authorId
////                result.tokens.refreshToken.size shouldBeGreaterThan 0
////                result.tokens.accessToken.size shouldBeGreaterThan 0
//            //endregion
//        }
//
//        And("incorrectly format email") {
//            val exception = shouldThrow<Exception> {
//                authorizationService.signup(genCreateAuthorRequest(aEmail = "email"))
//            }
//            // replace messages with enums
//            exception.message shouldBe "Not an email provided"
//        }
//
//        And("weak password") {
//            val exception = shouldThrow<Exception> {
//                authorizationService.signup(genCreateAuthorRequest(aPassword = "password"))
//            }
//            // replace messages with enums
//            exception.message shouldBe "weak password"
//        }
//
//        And("taken email") {
//            val exception = shouldThrow<Exception> {
//                authorizationService.signup(genCreateAuthorRequest(aPassword = "password"))
//            }
//            // replace messages with enums
//            exception.message shouldBe "email taken"
//        }
//
//        And("taken username") {
//            val exception = shouldThrow<Exception> {
//                authorizationService.signup(genCreateAuthorRequest(aPassword = "password"))
//            }
//            // replace messages with enums
//            exception.message shouldBe "email taken"
//        }
//
//    }

//    given("login user") {
//
//        And("correct credentials") {
//            val result = authorizationService.login("email", "password")
//            result.authorId shouldNotBe null
////                result.RefreshToken.length shouldNotBe 0
////                result.accessToken.length shouldNotBe 0
//        }
//
//        And("invalid email") {
//            val exception = shouldThrow<Exception> {
//                authorizationService.login("invalid", "correctPassword")
//            }
//            exception.message shouldBe "invalid email format"
//        }
//
//        And("password provided is invalid") {
//            var exception = shouldThrow<Exception> {
//                authorizationService.login("correctEmail", "invalid")
//            }
//            exception.message shouldBe "invalid password format"
//        }
//
//    }

//    given("refreshAccessToken") { }
//
//    given("requestPasswordReset") {
//
//        And("valid username and email") {
//            val result = authorizationService.requestPasswordReset(username, email)
//        }
//
//    }
//
//    given("setNewPasswordForSignup") { }
//
//    given("resetPasswordByEmail") { }
})
