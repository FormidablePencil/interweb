package unitTests.domainServices.login

import services.AuthorizationService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.mockk
import managers.interfaces.ITokenManager
import repositories.interfaces.IRefreshTokenRepository

class LoginUT : BehaviorSpec({
    val tokenManager: ITokenManager = mockk()
    val refreshTokenRepository: IRefreshTokenRepository = mockk()

    val loginDomainService = AuthorizationService(tokenManager, refreshTokenRepository)

    // mock all the dependencies
    // verify that the correct data in the dependencies has been inputted
    // verify that the correct data has been returned in all code paths

    Given("correct credentials") {
        Then("return and validate id and tokens") {
            var result = loginDomainService.login("email", "password")
            result.authorId shouldNotBe null
//                result.RefreshToken.length shouldNotBe 0
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
})