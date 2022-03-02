package com.idealIntent.repositories.compositions.carousels

import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesCreateReq
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes
import com.idealIntent.dtos.compositions.carousels.CarouselOfImagesComposePrepared
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.services.CompositionService
import com.squareup.kotlinpoet.FileSpec
import integrationTests.auth.flows.SignupFlow
import integrationTests.compositions.carousels.CarouselCompositionsFlow
import shared.kotlinPoet.compositionRepositoryKP.RepositoryTestBuilderDTO
import shared.kotlinPoet.compositionRepositoryKP.repositoryTestBuilder

fun carouselOfImagesRepositoryKP(): Pair<String, FileSpec> {
    return Pair(
        "src/test/kotlin/com/idealIntent/repositories",
        repositoryTestBuilder(
            dependencies = listOf(
                ImageRepository::class.java,
                TextRepository::class.java,
                SpaceRepository::class.java,
                CarouselOfImagesRepository::class.java,
                CompositionPrivilegesManager::class.java,
                CompositionService::class.java,
                SignupFlow::class.java,
                CarouselCompositionsFlow::class.java,
            ),
            dto = RepositoryTestBuilderDTO(
                compositionFlow = CarouselCompositionsFlow::class.java,
                compositionRepo = CarouselOfImagesRepository::class.java,
                responseData = CarouselBasicImagesRes::class.java,
                createRequestData = Pair(
                    "integrationTests.compositions.carousels.CarouselCompositionsFlow.Companion",
                    "carouselBasicImagesCreateReq"
                ),
                createRequest = CarouselBasicImagesCreateReq::class.java,
                composePrepared = CarouselOfImagesComposePrepared::class.java,
            ),
        ),
    )
}