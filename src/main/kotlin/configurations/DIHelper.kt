package configurations

import com.typesafe.config.ConfigFactory
import domainServices.AuthorsPortfolioDomainService
import domainServices.SignupDomainService
import domainServices.TokenDomainService
import io.ktor.config.*
import managers.AuthorsPortfolioManager
import managers.ITokenManager
import managers.TokenManager
import org.koin.dsl.module
import repositories.AuthorRepository
import repositories.IAuthorRepository

object DIHelper {
    val CoreModule = module {
        single<IAuthorRepository> { AuthorRepository() }
        // single { AuthorRepository() as IAuthorRepository }
        single { SignupDomainService(get(), get()) }
        single { AuthorsPortfolioManager() }
        single { AuthorsPortfolioDomainService(get(), get()) }
        single { TokenDomainService(get()) }
        single<ITokenManager> { TokenManager(get(), get()) }

        val applicationConfig = HoconApplicationConfig(ConfigFactory.load("application.conf"))
        single<IConfig> { Config(applicationConfig) }
    }
}