package unitTests.domainServices.signup

import dtos.author.CreateAuthorRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import managers.interfaces.ITokenManager
import models.profile.Author
import repositories.interfaces.IAuthorRepository
import services.AuthorizationService
import shared.BehaviorSpecUT

class SignupUT : BehaviorSpecUT({
    lateinit var authorizationService: AuthorizationService
    var tokenManager: ITokenManager = mockk()
    var authorRepository: IAuthorRepository = mockk()
    val authorId = 1
    val passwordId = 2
    val fakeAuthor = Author {
        val id = 3;
        val email = "email"
    }

//    every { authorizationService.generateTokens(any()) } returns TokensResult("", "")

    every { authorizationService.setNewPasswordForSignup(any()) } returns passwordId

    every { authorRepository.getByEmail(any()) } returns null
    every { authorRepository.getByUsername(any()) } returns null
    every { authorRepository.createAuthor(any()) } returns authorId


    fun genCreateAuthorRequest(
        username: String = "YourNeighborhoodSpider",
        email: String = "testemail12345@gmail.com",
        password: String = "Formidable!76"
    ): CreateAuthorRequest {
        return CreateAuthorRequest(username, email, "Billy", "Bob", password)
    }

    Given("valid credentials") {
        authorizationService = AuthorizationService(authorRepository, tokenManager)

        Then("return tokens and authorId") {

            val result = authorizationService.signup(genCreateAuthorRequest())

            //region assertions
//                result.authorId shouldBe authorId
//                result.tokens.refreshToken.size shouldBeGreaterThan 0
//                result.tokens.accessToken.size shouldBeGreaterThan 0
            //endregion
        }
    }

    Given("incorrectly format email") {
        Then("throw incorrectlyFormattedEmail exception") {
            val exception = shouldThrow<Exception> {
                authorizationService.signup(genCreateAuthorRequest(email = "email"))
            }
            // replace messages with enums
            exception.message shouldBe "Not an email provided"
        }
    }

    Given("weak password") {
        Then("throw weakPassword exception") {
            val exception = shouldThrow<Exception> {
                authorizationService.signup(genCreateAuthorRequest(password = "password"))
            }
            // replace messages with enums
            exception.message shouldBe "weak password"
        }
    }

    Given("taken email") {
        Then("throw exception") {
            val exception = shouldThrow<Exception> {
//                    signupDomainService.signup(genCreateAuthorRequest(password = "password"))
            }
            // replace messages with enums
            exception.message shouldBe "email taken"
        }
    }

    Given("taken username") {
        Then("throw exception") {
            val exception = shouldThrow<Exception> {
//                    signupDomainService.signup(genCreateAuthorRequest(password = "password"))
            }
            // replace messages with enums
            exception.message shouldBe "email taken"
        }
    }
})