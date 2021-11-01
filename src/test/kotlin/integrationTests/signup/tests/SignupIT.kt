package integrationTests.signup.tests

import integrationTests.signup.flows.SignupFlow
import org.koin.test.inject
import shared.testUtils.BehaviorSpecIT

class SignupIT : BehaviorSpecIT() {
    private val signupFlows: SignupFlow by inject()

    init {
        Given("valid credentials") {
            Then("return tokens and author id") {
                val result = signupFlows.signup()

//                result.authorId shouldNotBe null
//                result.tokens.refreshToken.size shouldBeGreaterThan 0
//                result.tokens.accessToken.size shouldBeGreaterThan 0
            }
        }
    }
}