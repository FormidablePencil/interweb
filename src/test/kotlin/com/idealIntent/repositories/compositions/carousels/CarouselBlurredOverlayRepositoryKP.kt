package com.idealIntent.repositories.compositions.carousels

import com.idealIntent.dtos.compositions.carousels.CarouselBlurredOverlayComposePrepared
import com.idealIntent.dtos.compositions.carousels.CarouselBlurredOverlayCreateReq
import com.idealIntent.dtos.compositions.carousels.CarouselBlurredOverlayRes
import com.idealIntent.services.CompositionService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MemberName
import integrationTests.auth.flows.SignupFlow
import integrationTests.compositions.carousels.CarouselCompositionsFlow
import shared.kotlinPoet.compositionRepositoryKP.RepositoryTestBuilderDTO
import shared.kotlinPoet.compositionRepositoryKP.repositoryTestBuilder
import java.io.File

fun main() {
    gridOneOffRepositoryKP()
        .writeTo(File("src/test/kotlin"))
//        .writeTo(System.out)
}

fun gridOneOffRepositoryKP(): FileSpec {
    return repositoryTestBuilder(
        packageName = "com.idealIntent.repositories.compositions.carousels",
        dependencies = listOf(
            CarouselBlurredOverlayRepository::class.java,
            CompositionService::class.java,
            SignupFlow::class.java,
            CarouselCompositionsFlow::class.java,
        ),
        dto = RepositoryTestBuilderDTO(
            compositionFlow = CarouselCompositionsFlow::class.java,
            compositionRepo = CarouselBlurredOverlayRepository::class.java,
            responseDataType = CarouselBlurredOverlayRes::class.java,
            responseData = MemberName(
                "integrationTests.compositions.carousels.CarouselCompositionsFlow.Companion",
                "carouselBasicImagesCreateReq"
            ),
            createRequest = CarouselBlurredOverlayCreateReq::class.java,
            composePrepared = CarouselBlurredOverlayComposePrepared::class.java,
        ),
        isCompComplex = true,
    )
}