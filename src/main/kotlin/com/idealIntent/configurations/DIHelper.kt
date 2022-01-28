package com.idealIntent.configurations

import com.typesafe.config.ConfigFactory
import io.ktor.config.*
import com.idealIntent.managers.AuthorsPortfolioManager
import com.idealIntent.managers.EmailManager
import com.idealIntent.managers.PasswordManager
import com.idealIntent.managers.TokenManager
import org.apache.commons.mail.SimpleEmail
import org.koin.dsl.module
import com.idealIntent.repositories.PasswordRepository
import com.idealIntent.repositories.RefreshTokenRepository
import com.idealIntent.repositories.codes.EmailVerificationCodeRepository
import com.idealIntent.repositories.codes.ResetPasswordCodeRepository
import com.idealIntent.repositories.profile.AccountRepository
import com.idealIntent.repositories.profile.AuthorProfileRelatedRepository
import com.idealIntent.repositories.profile.AuthorRepository
import com.idealIntent.services.AuthorizationService
import com.idealIntent.services.AuthorsPortfolioService
import java.io.FileInputStream
import java.util.*

object DIHelper {
    val CoreModule = module {
        // domain com.idealIntent.services
        single { AuthorsPortfolioService(get(), get()) }
        single { AuthorizationService(get(), get(), get(), get(), get(), get(), get()) }

        // com.idealIntent.managers
        single { AuthorsPortfolioManager() }
        single { TokenManager(get()) }
        single { PasswordManager(get(), get(), get()) }
        single { EmailManager(get(), get(), get()) }

        // com.idealIntent.repositories
        single { AuthorRepository() }
        single { PasswordRepository() }
        single { RefreshTokenRepository() }
        single { EmailVerificationCodeRepository() }
        single { ResetPasswordCodeRepository() }
        single { AccountRepository() }
        single { AuthorProfileRelatedRepository() }
        // env com.idealIntent.configurations
        val dbConnection = Properties()
        dbConnection.load(FileInputStream("local.datasource.properties"))

        val appConfig: ApplicationConfig = HoconApplicationConfig(ConfigFactory.load("application.conf"))

        single { AppEnv(appConfig, dbConnection) }
//        single<IConnectionToDb> { ConnectionToDb() } // database access

        // third parties
        single { SimpleEmail() } // e-mailer
    }
}