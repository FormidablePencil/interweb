package integrationTests.auth.tests

import integrationTests.auth.flows.SignupFlow
import org.koin.test.inject
import shared.testUtils.BehaviorSpecIT
import shared.testUtils.rollback

class SignupIT : BehaviorSpecIT({
    val signupFlow: SignupFlow by inject()

    Given("valid credentials") {
        Then("return tokens and author id") {
            rollback {
                signupFlow.signup()
            }
        }
    }
})