package unitTests.domainServices.authorization


import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import managers.TokenManager
import repositories.interfaces.IAuthorRepository
import repositories.interfaces.IRefreshTokenRepository

class RefreshAccessToken : BehaviorSpec({
    val authorRepository: IAuthorRepository = mockk()
    val refreshTokenRepository: IRefreshTokenRepository = mockk()

    val tokenManager = TokenManager(authorRepository, refreshTokenRepository)

    Given("valid refresh token") {
        Then("return access token") {
            val (accessToken, refreshToken) = tokenManager.refreshAccessToken("invalidToken", 2)

            // region assertion
            accessToken.length shouldBeGreaterThan 0
            refreshToken.length shouldBeGreaterThan 0
            // endregion
        }
    }

    Given("invalid refresh token") {
        Then("throw custom exception") {
            val exception = shouldThrow<Exception> {
                tokenManager.refreshAccessToken("invalidToken", 2)
            }
            exception.message shouldBe "invalid refresh token"
        }
    }
})
