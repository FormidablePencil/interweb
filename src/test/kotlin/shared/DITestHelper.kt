package shared

import domainServices.SignupDomainService
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import shared.mockFactories.authorRepositoryMK
import shared.mockFactories.authorizationManagerMK
import shared.mockFactories.tokenManagerMK

class DITestHelper {
    companion object {
        val CoreModule = module {
//        single { LoginFlows() }
//        single { SignupFlows() }
//        single { TokenFlows() }
        }

        val UnitTestModule = module {
            single { authorRepositoryMK() }
            single { authorizationManagerMK() }
            single { tokenManagerMK() }

            single { SignupDomainService(get(), get(), get()) }
        }

        fun overrideAndStart(module: Module) {
            stopKoin()
            startKoin {
                modules(
                    UnitTestModule,
                    module
                )
            }
        }
    }
}
