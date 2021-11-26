package shared

import integrationTests.auth.flows.LoginFlow
import integrationTests.auth.flows.SignupFlow
import org.koin.dsl.module

object DITestHelper {
    val FlowModule = module {
        single { SignupFlow() }
        single { LoginFlow() }
    }

//    val UnitTestModule = module {
//        single { connectionToDbMK() }
//        single { appEnvMK() }
//        single { mockk<SimpleEmail>(relaxed = true) }
//    }
}