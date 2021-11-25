package configurations

import com.typesafe.config.ConfigFactory
import configurations.interfaces.IAppEnv
import configurations.interfaces.IConnectionToDb
import io.ktor.config.*
import managers.AuthorsPortfolioManager
import managers.EmailManager
import managers.PasswordManager
import managers.TokenManager
import managers.interfaces.IEmailManager
import managers.interfaces.IPasswordManager
import managers.interfaces.ITokenManager
import org.apache.commons.mail.SimpleEmail
import org.koin.dsl.module
import repositories.AuthorRepository
import repositories.EmailVerifyCodeRepository
import repositories.PasswordRepository
import repositories.RefreshTokenRepository
import repositories.interfaces.IAuthorRepository
import repositories.interfaces.IEmailVerifyCodeRepository
import repositories.interfaces.IPasswordRepository
import repositories.interfaces.IRefreshTokenRepository
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
        single<ITokenManager> { TokenManager(get()) }
        single<IPasswordManager> { PasswordManager(get(), get(), get()) }
        single<IEmailManager> { EmailManager(get(), get()) }
        single<IEmailVerifyCodeRepository> { EmailVerifyCodeRepository() }

        // repositories
        single<IAuthorRepository> { AuthorRepository() }
        single<IPasswordRepository> { PasswordRepository() }
        single<IRefreshTokenRepository> { RefreshTokenRepository() }

        // env configurations
        val dbConnection = Properties()
        dbConnection.load(FileInputStream("local.datasource.properties"))

        val appConfig: ApplicationConfig = HoconApplicationConfig(ConfigFactory.load("application.conf"))

        single<IAppEnv> { AppEnv(appConfig, dbConnection) }

        // other
        single<IConnectionToDb> { ConnectionToDb() } // database access
        single { SimpleEmail() } // e-mailer
    }
}