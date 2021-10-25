package unitTests.domainServices

import domainServices.SignupDomainService
import dto.author.CreateAuthorRequest
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import org.koin.test.inject
import shared.KoinBehaviorSpec

class SignupUnitTests : KoinBehaviorSpec() {
    private val signupDomainService: SignupDomainService by inject()

    fun genCreateAuthorRequest(
        username: String = "username", email: String = "email", password: String = "password"
    ): CreateAuthorRequest {
        return CreateAuthorRequest(username, email, "Billy", "Bob", password)
    }

    init {
        Given("valid credentials") {
            Then("return tokens and authorId") {
                //region setup
                // dependency inject the mocked data on the fly using koin's scope
                //endregion

                signupDomainService.signup(genCreateAuthorRequest())

                //region assertions
                // test that dependencies have been called and the data provided was correct
                //endregion
            }
        }

        Given("incorrectly format email") {
            Then("throw incorrectlyFormattedEmail exception") {
                var exception = shouldThrow<Exception> {
                    signupDomainService.signup(genCreateAuthorRequest(email = "email"))
                }
                // replace messages with enums
                exception.message shouldBe "incorrectly formatted email"
            }
        }

        Given("weak password") {
            Then("throw weakPassword exception") {
                var exception = shouldThrow<Exception> {
                    signupDomainService.signup(genCreateAuthorRequest(password = "password"))
                }
                // replace messages with enums
                exception.message shouldBe "weak password"
            }
        }
    }
}