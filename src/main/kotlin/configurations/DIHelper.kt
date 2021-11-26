package configurations

import com.typesafe.config.ConfigFactory
import io.ktor.config.*
import managers.AuthorsPortfolioManager
import managers.EmailManager
import managers.PasswordManager
import managers.TokenManager
import org.apache.commons.mail.SimpleEmail
import org.koin.dsl.module
import repositories.AuthorRepository
import repositories.codes.EmailVerificationCodeRepository
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
        single { EmailManager(get(), get(), get()) }
        single { EmailVerificationCodeRepository() }

        // repositories
        single { AuthorRepository() }
        single { PasswordRepository() }
        single { RefreshTokenRepository() }

        // env configurations
        val dbConnection = Properties()
        dbConnection.load(FileInputStream("local.datasource.properties"))

        val appConfig: ApplicationConfig = HoconApplicationConfig(ConfigFactory.load("application.conf"))

        single { AppEnv(appConfig, dbConnection) }
//        single<IConnectionToDb> { ConnectionToDb() } // database access

        // third parties
        single { SimpleEmail() } // e-mailer
    }
}