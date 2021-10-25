package configurations

import com.typesafe.config.ConfigFactory
import domainServices.AuthorsPortfolioDomainService
import domainServices.LoginDomainService
import domainServices.SignupDomainService
import domainServices.TokenDomainService
import helper.DbHelper
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
        single { SignupDomainService(get(), get(), get(), get()) }
        single { AuthorsPortfolioDomainService(get(), get()) }
        single { TokenDomainService(get()) }
        single { LoginDomainService(get(), get()) }

        // managers
        single { AuthorsPortfolioManager() }
        single<ITokenManager> { TokenManager(get(), get(), get()) }
        single<IAuthorizationManager> { AuthorizationManager(get(), get()) }

        // repositories
        single<IAuthorRepository> { AuthorRepository() }
        single<IPasswordRepository> { PasswordRepository() }
        single<ITokenRepository> { TokenRepository() }

        // env configurations
        val dbConnection = Properties()
        dbConnection.load(FileInputStream("local.datasource.properties"))
        val applicationConfig = HoconApplicationConfig(ConfigFactory.load("application.conf"))

        single<IConfig> { Config(applicationConfig, dbConnection) }
        single { DbHelper() }

        // other
        single { PassEncrypt(get()) }
    }
}