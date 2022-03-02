package com.idealIntent.configurations

import com.idealIntent.managers.*
import com.idealIntent.managers.compositions.banners.BannerImageManager
import com.idealIntent.managers.compositions.banners.BannersManager
import com.idealIntent.managers.compositions.carousels.CarouselBlurredOverlayManager
import com.idealIntent.managers.compositions.carousels.CarouselOfImagesManager
import com.idealIntent.managers.compositions.carousels.CarouselsManager
import com.idealIntent.managers.compositions.grids.GridOneOffManager
import com.idealIntent.managers.compositions.grids.GridsManager
import com.idealIntent.managers.compositions.headers.HeaderBasicManager
import com.idealIntent.managers.compositions.headers.HeadersManager
import com.idealIntent.managers.compositions.images.D2ImageRepository
import com.idealIntent.managers.compositions.texts.D2TextRepository
import com.idealIntent.managers.compositions.texts.TextLonelyManager
import com.idealIntent.managers.compositions.texts.TextsManager
import com.idealIntent.repositories.PasswordRepository
import com.idealIntent.repositories.RefreshTokenRepository
import com.idealIntent.repositories.codes.EmailVerificationCodeRepository
import com.idealIntent.repositories.codes.ResetPasswordCodeRepository
import com.idealIntent.repositories.collectionsGeneric.CompositionSourceRepository
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.CompositionQueryBuilder
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.repositories.compositions.banners.BannerImageRepository
import com.idealIntent.repositories.compositions.carousels.CarouselBlurredOverlayRepository
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import com.idealIntent.repositories.compositions.grids.GridOneOffRepository
import com.idealIntent.repositories.compositions.headers.HeaderBasicRepository
import com.idealIntent.repositories.compositions.texts.TextLonelyRepository
import com.idealIntent.repositories.profile.AccountRepository
import com.idealIntent.repositories.profile.AuthorProfileRelatedRepository
import com.idealIntent.repositories.profile.AuthorRepository
import com.idealIntent.services.AuthorizationService
import com.idealIntent.services.AuthorsPortfolioService
import com.idealIntent.services.CompositionService
import com.typesafe.config.ConfigFactory
import io.ktor.config.*
import org.apache.commons.mail.SimpleEmail
import org.koin.dsl.module
import java.io.FileInputStream
import java.util.*

/**
 * Dependency injection singleton
 */
object DIHelper {
    val CoreModule = module {
        // services
        single { AuthorsPortfolioService(get(), get()) }
        single { AuthorizationService(get(), get(), get(), get(), get(), get(), get()) }
        single { CompositionService(get(), get(), get(), get(), get(), get(), get()) }

        // managers
        single { AuthorsPortfolioManager() }
        single { TokenManager(get()) }
        single { PasswordManager(get(), get(), get()) }
        single { EmailManager(get(), get(), get()) }
        single { CompositionPrivilegesManager(get(), get(), get()) }
        single { SpaceManager(get(), get(), get()) }

        // compositions
        single { CarouselsManager(get(), get()) }
        single { CarouselOfImagesManager(get(), get(), get(), get(), get(), get()) }
        single { CarouselBlurredOverlayManager(get(), get(), get(), get(), get(), get()) } // variant of carousel of images
        single { CarouselOfImagesRepository(get(), get()) }
        single { CarouselBlurredOverlayRepository() } // made up of CarouselOfImagesRepository

        single { TextsManager(get()) }
        single { TextLonelyManager(get(), get(), get()) }
        single { TextLonelyRepository() }

        single { BannersManager(get()) }
        single { BannerImageManager(get(), get(), get()) }
        single { BannerImageRepository() }

        single { GridsManager(get()) }
        single { GridOneOffManager(get(), get(), get(), get(), get(), get(), get(), get()) }
        single { GridOneOffRepository(get(), get()) }

        single { HeaderBasicManager(get(), get(), get()) }
        single { HeadersManager(get()) }
        single { HeaderBasicRepository() }

        // repositories
        single { AuthorRepository() }
        single { PasswordRepository() }
        single { RefreshTokenRepository() }
        single { EmailVerificationCodeRepository() }
        single { ResetPasswordCodeRepository() }
        single { AccountRepository() }
        single { AuthorProfileRelatedRepository() }
        single { CompositionSourceRepository() }
        single { TextRepository() }
        single { ImageRepository() }
        single { SpaceRepository() }
        single { D2ImageRepository(get()) }
        single { D2TextRepository(get()) }

        // env configurations
        single { CompositionQueryBuilder(get()) }

        val dbConnection = Properties()
        dbConnection.load(FileInputStream("local.datasource.properties"))

        val appConfig: ApplicationConfig = HoconApplicationConfig(ConfigFactory.load("application.conf"))

        single { AppEnv(appConfig, dbConnection) }
//        single<IConnectionToDb> { ConnectionToDb() } // database access

        // third parties
        single { SimpleEmail() } // e-mailer
    }
}