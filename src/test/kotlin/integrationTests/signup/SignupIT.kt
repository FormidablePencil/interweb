package integrationTests.signup

import io.kotest.matchers.shouldNotBe
import org.koin.test.inject
import shared.KoinBehaviorSpec

class SignupIT : KoinBehaviorSpec() {
    private val signupFlows: SignupFlows by inject()

    init {
        Given("valid credentials") {
            Then("return tokens and author id") {
                val result = signupFlows.signup()

                result.authorId shouldNotBe null
//                result.tokens.refreshToken.size shouldBeGreaterThan 0
//                result.tokens.accessToken.size shouldBeGreaterThan 0
            }
        }
    }
}