package com.idealIntent.repositories.compositions.texts

import com.idealIntent.dtos.compositions.texts.TextLonelyCreateReq
import com.idealIntent.dtos.compositions.texts.TextLonelyRes
import com.idealIntent.services.CompositionService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MemberName
import integrationTests.auth.flows.SignupFlow
import integrationTests.compositions.texts.TextCompositionsFlow
import shared.kotlinPoet.compositionRepositoryKP.RepositoryTestBuilderDTO
import shared.kotlinPoet.compositionRepositoryKP.repositoryTestBuilder
import java.io.File

fun main() {
    textLonelyRepositoryKP()
        .writeTo(File("src/test/kotlin"))
//        .writeTo(System.out)
}

fun textLonelyRepositoryKP(): FileSpec {
    return repositoryTestBuilder(
        packageName = "com.idealIntent.repositories.compositions.texts",
        dependencies = listOf(
            TextLonelyRepository::class.java,
            CompositionService::class.java,
            SignupFlow::class.java,
            TextCompositionsFlow::class.java,
        ),
        dto = RepositoryTestBuilderDTO(
            compositionFlow = TextCompositionsFlow::class.java,
            compositionRepo = TextLonelyRepository::class.java,
            responseDataType = TextLonelyRes::class.java,
            responseData = MemberName(
                "integrationTests.compositions.texts.TextCompositionsFlow.Companion",
                "privateTextLonelyCreateReq"
            ),
            createRequest = TextLonelyCreateReq::class.java,
            composePrepared = TextLonelyCreateReq::class.java,
        ),
        isCompComplex = false,
    )
}