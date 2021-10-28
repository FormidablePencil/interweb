package shared

import integrationTests.authorization.flows.LoginFlow
import integrationTests.authorization.flows.TokenFlow
import integrationTests.signup.flows.SignupFlow
import org.koin.dsl.module
import shared.mockFactories.appEnvMK
import shared.mockFactories.connectionToDbMK

object DITestHelper {
    val FlowModule = module {
        single { LoginFlow() }
        single { SignupFlow() }
        single { TokenFlow() }
    }

    // There are dependencies that are injected not through the constructors therefore we can use koin to handle them
    val UnitTestModule = module {
        single { connectionToDbMK() }
        single { appEnvMK() }
    }
}