package com.idealIntent.repositories.compositions.banners

import com.idealIntent.dtos.compositions.banners.BannerImageCreateReq
import com.idealIntent.dtos.compositions.banners.BannerImageRes
import com.idealIntent.dtos.compositions.carousels.CarouselOfImagesComposePrepared
import com.idealIntent.services.CompositionService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MemberName
import integrationTests.auth.flows.SignupFlow
import integrationTests.compositions.banners.BannerCompositionsFlow
import shared.kotlinPoet.compositionRepositoryKP.RepositoryTestBuilderDTO
import shared.kotlinPoet.compositionRepositoryKP.repositoryTestBuilder
import java.io.File

fun main() {
    bannerImageRepositoryKP()
        .writeTo(File("src/test/kotlin"))
//        .writeTo(System.out)
}

fun bannerImageRepositoryKP(): FileSpec {
    return repositoryTestBuilder(
        packageName = "com.idealIntent.repositories.compositions.banners",
        dependencies = listOf(
            BannerImageRepository::class.java,
            CompositionService::class.java,
            SignupFlow::class.java,
            BannerCompositionsFlow::class.java,
        ),
        dto = RepositoryTestBuilderDTO(
            compositionFlow = BannerCompositionsFlow::class.java,
            compositionRepo = BannerImageRepository::class.java,
            responseDataType = BannerImageRes::class.java,
            responseData = MemberName(
                "integrationTests.compositions.banners.BannerCompositionsFlow.Companion",
                "privateHeaderImageCreateReq"
            ),
            createRequest = BannerImageCreateReq::class.java,
            composePrepared = CarouselOfImagesComposePrepared::class.java
        ),
        isCompComplex = false,
    )
}