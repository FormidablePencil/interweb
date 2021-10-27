package configurations

import com.typesafe.config.ConfigFactory
import domainServices.AuthorsPortfolioDomainService
import domainServices.LoginDomainService
import domainServices.SignupDomainService
import domainServices.AuthorizationService
import helper.PassEncrypt
import io.ktor.config.*
import managers.*
import org.koin.dsl.module
import repositories.*
import java.io.FileInputStream
import java.util.*

object DIHelper {
    val CoreModule = module {
        // domain services
        single { SignupDomainService(get(), get(), get()) }
        single { AuthorsPortfolioDomainService(get(), get()) }
        single { AuthorizationService(get()) }
        single { LoginDomainService(get(), get()) }

        // managers
        single { AuthorsPortfolioManager() }
        single<ITokenManager> { TokenManager(get(), get()) }
        single<IAuthorizationManager> { AuthorizationManager(get(), get(), get()) }

        // repositories
        single<IAuthorRepository> { AuthorRepository() }
        single<IPasswordRepository> { PasswordRepository() }
        single<ITokenRepository> { TokenRepository() }

        // env configurations
        val dbConnection = Properties()
        dbConnection.load(FileInputStream("local.datasource.properties"))

        val appConfig: ApplicationConfig = HoconApplicationConfig(ConfigFactory.load("application.conf"))

        single<IAppEnv> { AppEnv(appConfig, dbConnection) }

        // other
        single { PassEncrypt() } // helper
        single<IConnectionToDb> { ConnectionToDb() } //database access
    }
}