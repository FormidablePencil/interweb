package unitTests.domainServices

import arrow.core.success
import domainServices.SignupDomainService
import dto.*
import dto.author.CreateAuthorRequest
import dto.signup.SignupResult
import dto.signup.SignupResultError
import dto.token.TokensResult
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.mockk.every
import io.mockk.mockk
import managers.IAuthorizationManager
import managers.ITokenManager
import models.Author
import org.koin.test.KoinTest
import repositories.IAuthorRepository
import shared.KoinBehaviorSpec

class SignupUT : KoinBehaviorSpec() {
    lateinit var signupDomainService: SignupDomainService
    var authorizationManager: IAuthorizationManager = mockk()
    var tokenManager: ITokenManager = mockk()
    var authorRepository: IAuthorRepository = mockk()
    val authorId = 1
    val passwordId = 2
    val fakeAuthor = Author {
        val id = 3;
        val email = "email"
    }

    init {
        every { tokenManager.generateTokens(any(), any()) } returns TokensResult(HashMap(1), HashMap(1))

        every { authorizationManager.setNewPassword(any()) } returns passwordId

        every { authorRepository.getByEmail(any()) } returns null
        every { authorRepository.getByUsername(any()) } returns null
        every { authorRepository.createAuthor(any()) } returns authorId
    }

    private fun genCreateAuthorRequest(
        username: String = "YourNeighborhoodSpider",
        email: String = "testemail12345@gmail.com",
        password: String = "Formidable!76"
    ): CreateAuthorRequest {
        return CreateAuthorRequest(username, email, "Billy", "Bob", password)
    }

    init {
        Given("valid credentials") {
            signupDomainService = SignupDomainService(authorizationManager, authorRepository, tokenManager)

            Then("return tokens and authorId") {

                val result = signupDomainService.signup(genCreateAuthorRequest())

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
                    signupDomainService.signup(genCreateAuthorRequest(email = "email"))
                }
                // replace messages with enums
                exception.message shouldBe "Not an email provided"
            }
        }

        Given("weak password") {
            Then("throw weakPassword exception") {
                val exception = shouldThrow<Exception> {
                    signupDomainService.signup(genCreateAuthorRequest(password = "password"))
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
    }
}