package configurations

import com.typesafe.config.ConfigFactory
import configurations.interfaces.IConnectionToDb
import io.ktor.config.*
import managers.AuthorsPortfolioManager
import managers.EmailManager
import managers.PasswordManager
import managers.TokenManager
import org.koin.dsl.module
import repositories.AuthorRepository
import repositories.EmailRepository
import repositories.PasswordRepository
import repositories.RefreshTokenRepository
import services.AuthorizationService
import services.AuthorsPortfolioService
import java.io.FileInputStream
import java.util.*

object DIHelper {
    val CoreModule = module {
        // domain services
        single { AuthorsPortfolioService(get(), get()) }
        single { AuthorizationService(get(), get(), get(), get(), get()) }

        // managers
        single { AuthorsPortfolioManager() }
        single { TokenManager(get()) }
        single { PasswordManager(get(), get(), get()) }
        single { EmailManager(get(), get()) }
        single { EmailRepository() }

        // repositories
        single { AuthorRepository() }
        single { PasswordRepository() }
        single { RefreshTokenRepository() }

        // env configurations
        val dbConnection = Properties()
        dbConnection.load(FileInputStream("local.datasource.properties"))

        val appConfig: ApplicationConfig = HoconApplicationConfig(ConfigFactory.load("application.conf"))

        single { AppEnv(appConfig, dbConnection) }
        single<IConnectionToDb> { ConnectionToDb() } // database access

        // other
//        single { SimpleEmail() } // e-mailer
    }
}