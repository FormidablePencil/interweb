package com.idealIntent.repositories.compositions.grids

import com.idealIntent.dtos.compositions.grids.GridOneOffComposePrepared
import com.idealIntent.dtos.compositions.grids.GridOneOffCreateReq
import com.idealIntent.dtos.compositions.grids.GridOneOffRes3
import com.idealIntent.services.CompositionService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MemberName
import integrationTests.auth.flows.SignupFlow
import integrationTests.compositions.grids.GridCompositionsFlow
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
        packageName = "com.idealIntent.repositories.compositions.grids",
        dependencies = listOf(
            GridOneOffRepository::class.java,
            CompositionService::class.java,
            SignupFlow::class.java,
            GridCompositionsFlow::class.java,
        ),
        dto = RepositoryTestBuilderDTO(
            compositionFlow = GridCompositionsFlow::class.java,
            compositionRepo = GridOneOffRepository::class.java,
            responseDataType = GridOneOffRes3::class.java,
            responseData = MemberName(
                "integrationTests.compositions.grids.GridCompositionsFlow.Companion",
                "privateGridOneOffReq"
            ),
            createRequest = GridOneOffCreateReq::class.java,
            composePrepared = GridOneOffComposePrepared::class.java,
        ),
        isCompComplex = true,
    )
}