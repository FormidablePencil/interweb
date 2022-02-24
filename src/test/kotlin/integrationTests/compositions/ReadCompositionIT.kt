package integrationTests.compositions

import integrationTests.auth.flows.AuthUtilities
import integrationTests.auth.flows.SignupFlow
import org.koin.core.component.inject
import shared.testUtils.BehaviorSpecIT

class ReadCompositionIT : BehaviorSpecIT({
    val signupFlow: SignupFlow by inject()


    given("create compositions of all variants") {
        then("get compositions") {
            val authorId = signupFlow.signupReturnId(AuthUtilities.createAuthorRequest)


        }
    }
})