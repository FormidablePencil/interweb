package integrationTests.auth.tests

import integrationTests.auth.flows.SignupFlow
import org.koin.test.inject
import shared.testUtils.BehaviorSpecIT
import shared.testUtils.rollback

class SignupIT : BehaviorSpecIT({
    val signupFlows: SignupFlow by inject()

    Given("valid credentials") {
        Then("return tokens and author id") {
            rollback {
                val result = signupFlows.signup()
            }
        }
    }
})