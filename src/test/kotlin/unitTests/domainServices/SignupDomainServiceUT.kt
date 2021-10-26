package unitTests.domainServices

import domainServices.SignupDomainService
import dto.author.CreateAuthorRequest
import dto.token.TokensResult
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import managers.IAuthorizationManager
import managers.ITokenManager
import models.Author
import org.koin.dsl.module
import org.koin.test.KoinTest
import repositories.IAuthorRepository
import shared.DITestHelper

class SignupUT : BehaviorSpec(), KoinTest {
    private lateinit var signupDomainService: SignupDomainService

    private fun genCreateAuthorRequest(
        username: String = "YourNeighborhoodSpider",
        email: String = "testemail12345@gmail.com",
        password: String = "Formidable!76"
    ): CreateAuthorRequest {
        return CreateAuthorRequest(username, email, "Billy", "Bob", password)
    }

    init {
        // override mocks in here
        Given("valid credentials") {
            val tokenManager = mockk<ITokenManager>()
            every { tokenManager.generateTokens(any(), any()) } returns TokensResult(HashMap(1), HashMap(1))
            val authorizationManager = mockk<IAuthorizationManager>()
            every { authorizationManager.setNewPassword(any()) } returns 123542
            DITestHelper.overrideAndStart(module { single { tokenManager }; single { authorizationManager } })
            val authorRepository = mockk<IAuthorRepository>()
            val fakeAuthor = Author {
                val id = 1
                val email = "email"
            }
            every { authorRepository.getByEmail(any()) } returns fakeAuthor
            every { authorRepository.getByUsername(any()) } returns fakeAuthor
            every { authorRepository.createAuthor(any()) } returns 2133

            Then("return tokens and authorId") {

                signupDomainService = SignupDomainService(authorizationManager, authorRepository, tokenManager)
                val result = signupDomainService.signup(genCreateAuthorRequest())

                //region assertions
                // test that dependencies have been called and the data provided was correct
                //endregion
            }
        }

        Given("incorrectly format email") {
            val tokenManager = mockk<ITokenManager>()
            every { tokenManager.generateTokens(any(), any()) } returns TokensResult(HashMap(1), HashMap(1))
            val authorizationManager = mockk<IAuthorizationManager>()
            every { authorizationManager.setNewPassword(any()) } returns 123542
            val authorRepository = mockk<IAuthorRepository>()
            val fakeAuthor = Author {
                val id = 1
                val email = "email"
            }
            every { authorRepository.getByEmail(any()) } returns fakeAuthor
            every { authorRepository.getByUsername(any()) } returns fakeAuthor
            every { authorRepository.createAuthor(any()) } returns 2133

            Then("throw incorrectlyFormattedEmail exception") {
                val exception = shouldThrow<Exception> {
                    signupDomainService.signup(genCreateAuthorRequest(email = "email"))
                }
                // replace messages with enums
                exception.message shouldBe "Not an email provided"
            }
        }

        Given("weak password") {
            val tokenManager = mockk<ITokenManager>()
            every { tokenManager.generateTokens(any(), any()) } returns TokensResult(HashMap(1), HashMap(1))
            val authorizationManager = mockk<IAuthorizationManager>()
            every { authorizationManager.setNewPassword(any()) } returns 123542
            DITestHelper.overrideAndStart(module { single { tokenManager }; single { authorizationManager } })

            Then("throw weakPassword exception") {
                val exception = shouldThrow<Exception> {
                    signupDomainService.signup(genCreateAuthorRequest(password = "password"))
                }
                // replace messages with enums
                exception.message shouldBe "weak password"
            }
        }

        Given("taken email") {
            val tokenManager = mockk<ITokenManager>()
            every { tokenManager.generateTokens(any(), any()) } returns TokensResult(HashMap(1), HashMap(1))
            val authorizationManager = mockk<IAuthorizationManager>()
            every { authorizationManager.setNewPassword(any()) } returns 123542
            DITestHelper.overrideAndStart(module { single { tokenManager }; single { authorizationManager } })

            Then("throw exception") {
                val exception = shouldThrow<Exception> {
//                    signupDomainService.signup(genCreateAuthorRequest(password = "password"))
                }
                // replace messages with enums
                exception.message shouldBe "email taken"
            }
        }

        Given("taken username") {
            val tokenManager = mockk<ITokenManager>()
            every { tokenManager.generateTokens(any(), any()) } returns TokensResult(HashMap(1), HashMap(1))
            val authorizationManager = mockk<IAuthorizationManager>()
            every { authorizationManager.setNewPassword(any()) } returns 123542
            DITestHelper.overrideAndStart(module { single { tokenManager }; single { authorizationManager } })

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