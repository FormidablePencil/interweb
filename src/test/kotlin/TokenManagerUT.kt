import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import managers.TokenManager
import org.koin.test.inject
import shared.KoinBehaviorSpec

open class TokenManagerUT : KoinBehaviorSpec() {
    val tokenManager by inject<TokenManager>()
}

class RefreshAccessToken : TokenManagerUT() {
    init {
        Given("valid refresh token") {
            Then("return access token") {
                val (accessToken, refreshToken) = tokenManager.refreshAccessToken("invalidToken")

                // region assertion
                accessToken.length shouldBeGreaterThan 0
                refreshToken.length shouldBeGreaterThan 0
                // endregion
            }
        }

        Given("invalid refresh token") {
            Then("throw custom exception") {
                val exception = shouldThrow<Exception> {
                    tokenManager.refreshAccessToken("invalidToken")
                }
                exception.message shouldBe "invalid refresh token"
            }
        }
    }
}