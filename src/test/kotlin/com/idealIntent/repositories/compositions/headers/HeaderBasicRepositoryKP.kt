package com.idealIntent.repositories.compositions.headers

import com.idealIntent.dtos.compositions.headers.HeaderBasicCreateReq
import com.idealIntent.dtos.compositions.headers.HeaderBasicRes
import com.idealIntent.services.CompositionService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MemberName
import integrationTests.auth.flows.SignupFlow
import integrationTests.compositions.headers.HeaderCompositionsFlow
import shared.kotlinPoet.compositionRepositoryKP.RepositoryTestBuilderDTO
import shared.kotlinPoet.compositionRepositoryKP.repositoryTestBuilder
import java.io.File

fun main() {
    headerBasicRepositoryKP()
        .writeTo(File("src/test/kotlin"))
//        .writeTo(System.out)
}

fun headerBasicRepositoryKP(): FileSpec {
    return repositoryTestBuilder(
        packageName = "com.idealIntent.repositories.compositions.headers",
        dependencies = listOf(
            HeaderBasicRepository::class.java,
            CompositionService::class.java,
            SignupFlow::class.java,
            HeaderCompositionsFlow::class.java,
        ),
        dto = RepositoryTestBuilderDTO(
            compositionFlow = HeaderCompositionsFlow::class.java,
            compositionRepo = HeaderBasicRepository::class.java,
            responseDataType = HeaderBasicRes::class.java,
            responseData = MemberName(
                "integrationTests.compositions.grids.HeaderCompositionsFlow.Companion",
                "privateHeaderBasicReq"
            ),
            createRequest = HeaderBasicCreateReq::class.java,
            composePrepared = HeaderBasicCreateReq::class.java,
        ),
        isCompComplex = false,
    )
}