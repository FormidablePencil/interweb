package integrationTests.authorization

import configurations.DIHelper
import integrationTests.signup.SignupFlows
import io.kotlintest.shouldNotBe
import org.koin.core.context.startKoin
import org.koin.test.inject
import shared.DITestHelper
import shared.KoinBehaviorSpec
import shared.cleanup

class TokensIT : KoinBehaviorSpec() {
    private val signupFlows: SignupFlows by inject()

    init {
        startKoin {
            modules(DIHelper.CoreModule, DITestHelper.CoreModule)
        }
    }

    init {
        cleanup(true) {
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
}