package shared

import com.typesafe.config.ConfigFactory
import configurations.AppEnv
import configurations.ConnectionToDb
import configurations.IAppEnv
import configurations.IConnectionToDb
import domainServices.SignupDomainService
import integrationTests.authorization.AuthorizationFlows
import integrationTests.login.LoginFlows
import integrationTests.signup.SignupFlows
import io.ktor.config.*
import io.mockk.every
import io.mockk.mockk
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import shared.mockFactories.*

class DITestHelper {
    companion object {
        val CoreModule = module {
            single { LoginFlows() }
            single { SignupFlows() }
            single { AuthorizationFlows() }
        }

        val UnitTestModule = module {
//            single { authorRepositoryMK() }
//            single { authorizationManagerMK() }
//            single { tokenManagerMK() }
//            single { SignupDomainService(get(), get(), get()) }

            single { connectionToDbMK() }
            single { appEnvMK() }
        }

        // For unit tests specifically. It's if you want DI mocked dependencies
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
