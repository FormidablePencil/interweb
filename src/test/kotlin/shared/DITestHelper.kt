package shared

import org.koin.dsl.module
import integrationTests.login.LoginFlows
import integrationTests.signup.SignupFlows
import integrationTests.token.TokenFlows

object DITestHelper {
    val CoreModule = module {
//        single { LoginFlows() }
//        single { SignupFlows() }
//        single { TokenFlows() }
    }
}
