package integrationTests.token

import domainServices.TokenDomainService
import integrationTests.login.LoginFlows
import org.koin.test.inject
import shared.KoinFunSpec

class KotlinTest : KoinFunSpec() {
    val loginFlows = LoginFlows()
    val tokenFlows = TokenFlows()
    val tokenDomainService by inject<TokenDomainService>()

    init {
        test("String length should return the length of a string") {
            tokenFlows.loginTokens_flow()
        }
    }
}
