package integrationTests.auth.tests

import integrationTests.auth.flows.LoginFlow
import org.koin.test.inject
import shared.testUtils.BehaviorSpecIT
import shared.testUtils.rollback

class LoginIT : BehaviorSpecIT({
    val loginFlow: LoginFlow by inject()

    Given("login") {
        Then("by simpleEmail") {
            rollback {
                loginFlow.loginByEmail()
            }
        }
        Then("username") {
            rollback {
                loginFlow.loginByUsername()
            }
        }
    }
}
)

