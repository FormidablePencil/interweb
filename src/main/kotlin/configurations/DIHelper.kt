package configurations

import com.typesafe.config.ConfigFactory
import domainServices.AuthorsPortfolioDomainService
import domainServices.LoginDomainService
import domainServices.SignupDomainService
import domainServices.TokenDomainService
import io.ktor.config.*
import managers.AuthorsPortfolioManager
import managers.ITokenManager
import managers.TokenManager
import org.koin.dsl.module
import repositories.*

object DIHelper {
    val CoreModule = module {
        single { SignupDomainService(get(), get()) }
        single { AuthorsPortfolioManager() }
        single { AuthorsPortfolioDomainService(get(), get()) }
        single { TokenDomainService(get()) }
        single { LoginDomainService(get(), get()) }

        single<ITokenManager> { TokenManager(get(), get(), get()) }

        single<IAuthorRepository> { AuthorRepository() }
        single<IAuthorizationRepository> { AuthorizationRepository() }
        single<ITokenRepository> { TokenRepository() }

        val applicationConfig = HoconApplicationConfig(ConfigFactory.load("application.conf"))
        single<IConfig> { Config(applicationConfig) }
    }
}