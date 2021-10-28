package configurations

import com.typesafe.config.ConfigFactory
import configurations.interfaces.IAppEnv
import configurations.interfaces.IConnectionToDb
import services.AuthorsPortfolioService
import services.SignupService
import services.AuthorizationService
import helper.PassEncrypt
import io.ktor.config.*
import managers.*
import managers.interfaces.IAuthorizationManager
import managers.interfaces.ITokenManager
import org.koin.dsl.module
import repositories.*
import repositories.interfaces.IAuthorRepository
import repositories.interfaces.IPasswordRepository
import repositories.interfaces.ITokenRepository
import java.io.FileInputStream
import java.util.*

object DIHelper {
    val CoreModule = module {
        // domain services
        single { SignupService(get(), get(), get()) }
        single { AuthorsPortfolioService(get(), get()) }
        single { AuthorizationService(get(), get(), get()) }

        // managers
        single { AuthorsPortfolioManager() }
        single<ITokenManager> { TokenManager(get()) }
        single<IAuthorizationManager> { AuthorizationManager(get(), get(), get(), get(), get()) }

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