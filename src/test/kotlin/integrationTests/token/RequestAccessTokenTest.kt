package integrationTests.token

import domainServices.TokenDomainService
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import org.koin.test.inject
import shared.KoinBehaviorSpec

class RequestAccessTokenTest : KoinBehaviorSpec() {
    private val tokenDomainService: TokenDomainService by inject()

    init {
        Given("a valid refresh token") {
            Then("return new access token and refresh token") {
                var (refreshToken, accessToken) = tokenDomainService.refreshAccessToken("refresh token")

                // region assertions
                refreshToken.length shouldBeGreaterThan 0
                accessToken.length shouldBeGreaterThan 0
                // endregion
            }
        }
    }
}