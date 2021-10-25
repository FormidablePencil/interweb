package integrationTests.token

import domainServices.LoginDomainService
import domainServices.TokenDomainService
import integrationTests.login.LoginFlows
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import org.koin.test.inject
import shared.KoinBehaviorSpec
import shared.KoinFunSpec

class TokenTests : KoinBehaviorSpec() {
    val loginDomainService: LoginDomainService by inject()

    init {
        Given("a logged in") {
            Then("user should get l") {
                var result = loginDomainService.login("", "")
            }
        }
    }
}

class RequestAccessToken : KoinBehaviorSpec() {
    val tokenDomainService: TokenDomainService by inject()

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