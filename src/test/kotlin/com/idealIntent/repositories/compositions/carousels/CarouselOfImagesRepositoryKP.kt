package com.idealIntent.repositories.compositions.carousels

import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesCreateReq
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes
import com.idealIntent.dtos.compositions.carousels.CarouselOfImagesComposePrepared
import com.idealIntent.services.CompositionService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MemberName
import integrationTests.auth.flows.SignupFlow
import integrationTests.compositions.carousels.CarouselCompositionsFlow
import shared.kotlinPoet.compositionRepositoryKP.RepositoryTestBuilderDTO
import shared.kotlinPoet.compositionRepositoryKP.repositoryTestBuilder
import java.io.File

fun main() {
    val (writeTo, func) = carouselOfImagesRepositoryKP()
    func
        .writeTo(File("src/test/kotlin"))
//        .writeTo(System.out)
}

fun carouselOfImagesRepositoryKP(): Pair<String, FileSpec> {
    return Pair(
        "src/test/kotlin/com/idealIntent/repositories",
        repositoryTestBuilder(
            packageName = "com.idealIntent.repositories.compositions.carousels",
            dependencies = listOf(
                CarouselOfImagesRepository::class.java,
                CompositionService::class.java,
                SignupFlow::class.java,
                CarouselCompositionsFlow::class.java,
            ),
            dto = RepositoryTestBuilderDTO(
                compositionFlow = CarouselCompositionsFlow::class.java,
                compositionRepo = CarouselOfImagesRepository::class.java,
                responseDataType = CarouselBasicImagesRes::class.java,
                responseData = MemberName(
                    "integrationTests.compositions.carousels.CarouselCompositionsFlow.Companion",
                    "carouselBasicImagesCreateReq"
                ),
                createRequest = CarouselBasicImagesCreateReq::class.java,
                composePrepared = CarouselOfImagesComposePrepared::class.java
            ),
            isCompComplex = true,
        ),
    )
}