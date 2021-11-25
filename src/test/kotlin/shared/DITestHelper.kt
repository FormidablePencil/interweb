package shared

import integrationTests.auth.flows.LoginFlow
import integrationTests.auth.flows.SignupFlow
import io.mockk.mockk
import org.apache.commons.mail.SimpleEmail
import org.koin.dsl.module
import shared.mockFactories.appEnvMK
import shared.mockFactories.connectionToDbMK

object DITestHelper {
    val FlowModule = module {
        single { SignupFlow() }
        single { LoginFlow() }
    }

    val UnitTestModule = module {
        single { connectionToDbMK() }
        single { appEnvMK() }
        single { mockk<SimpleEmail>(relaxed = true) }
    }
}