package unitTests.domainServices

import domainServices.LoginDomainService
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import org.koin.test.inject
import shared.KoinBehaviorSpec

class LoginUT : KoinBehaviorSpec() {
    private val loginDomainService by inject<LoginDomainService>()

    init {
        // mock all the dependencies
        // verify that the correct data in the dependencies has been inputted
        // verify that the correct data has been returned in all code paths

        Given("correct credentials") {
            Then("return and validate id and tokens") {
                var result = loginDomainService.login("email", "password")
                result.authorId shouldNotBe null
//                result.refreshToken.length shouldNotBe 0
//                result.accessToken.length shouldNotBe 0
            }
        }
        Given("invalid email") {
            val password = "correctPassword"
            val email = "invalid"
            Then("throw invalidEmail exception") {
                var exception = shouldThrow<Exception> {
                    loginDomainService.login(email, password)
                }
                exception.message shouldBe "invalid email format"
            }
        }

        Given("password provided is invalid") {
            val password = "invalid"
            val email = "correctEmail"
            Then("throw invalidPassword exception") {
                var exception = shouldThrow<Exception> {
                    loginDomainService.login(email, password)
                }
                exception.message shouldBe "invalid password format"
            }
        }
    }
}