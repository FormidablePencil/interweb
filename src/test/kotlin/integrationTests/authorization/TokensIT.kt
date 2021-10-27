package integrationTests.authorization

import integrationTests.signup.SignupFlows
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.shouldNotBe
import org.koin.test.inject
import shared.KoinBehaviorSpec

class TokensIT : KoinBehaviorSpec() {
    private val signupFlows: SignupFlows by inject()

    init {
        Given("created an account") {
            val result = signupFlows.signup()



            And("login") {
                // all the assertions happen in the flows
                // AuthorizationFlows.login()

            }

            And("refresh tokens") {
                // AuthorizationFlows.refresh()

                Then("login with new tokens given") {
                    // AuthorizationFlow.login()

                }
            }

            And("reset password") {
                Then("login with new tokens given") {
                    // AuthorizationFlows.login()

                }
            }
        }
    }
}