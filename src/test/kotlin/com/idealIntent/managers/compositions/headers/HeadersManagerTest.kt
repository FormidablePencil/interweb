package com.idealIntent.managers.compositions.headers

import com.idealIntent.dtos.compositions.headers.HeaderBasicRes
import dtos.compositions.headers.CompositionHeader
import integrationTests.compositions.headers.HeaderCompositionsFlow
import integrationTests.compositions.headers.HeaderCompositionsFlow.Companion.headerPublicBasicImagesReqSerialized
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import shared.testUtils.carouselPublicBasicImagesReqSerialized

class HeadersManagerTest : BehaviorSpec({
    val headerBasicManager: HeaderBasicManager = mockk()
    val layoutId = 543
    val compositionSourceId = 1
    val authorId = 1

    val headersManager = HeadersManager(headerBasicManager)

    val headerBasicRes = HeaderCompositionsFlow.privateHeaderBasicReq.let {
        HeaderBasicRes(
            id = 0,
            sourceId = compositionSourceId,
            bgImg = it.bgImg,
            profileImg = it.profileImg,
            privilegeLevel = it.privilegeLevel,
            name = it.name,
            privilegedAuthors = it.privilegedAuthors,
        )
    }

    beforeEach { clearAllMocks() }

    given("getPublicComposition") {
        // region setup
        every {
            headerBasicManager.getPublicComposition(
                compositionSourceId = compositionSourceId,
            )
        } returns headerBasicRes
        // endregion

        headersManager.getPublicComposition(
            compositionType = CompositionHeader.Basic,
            compositionSourceId = compositionSourceId,
        ) shouldBe headerBasicRes
    }

    given("getPrivateComposition") {
        // region setup
        every {
            headerBasicManager.getPrivateComposition(
                compositionSourceId = compositionSourceId,
                authorId = authorId
            )
        } returns headerBasicRes
        // endregion

        headersManager.getPrivateComposition(
            compositionType = CompositionHeader.Basic,
            compositionSourceId = compositionSourceId,
            authorId = authorId
        ) shouldBe headerBasicRes
    }

    given("createComposition") {
        // region setup
        val idOfNewlyCreatedComposition = 123
        val httpStatus = HttpStatusCode.Created
        every {
            headerBasicManager.createComposition(HeaderCompositionsFlow.publicHeaderBasicReq, layoutId, authorId)
        } returns idOfNewlyCreatedComposition
        // endregion setup

        val res = headersManager.createComposition(
            CompositionHeader.Basic, headerPublicBasicImagesReqSerialized, layoutId, authorId
        )

        res shouldBe idOfNewlyCreatedComposition
    }

    given("updateComposition") {
        // region Setup
        justRun {
            headerBasicManager.updateComposition(listOf(), compositionSourceId, authorId)
        }
        // endregion
        headersManager.updateComposition(CompositionHeader.Basic, compositionSourceId, listOf(), authorId)
    }

    given("deleteComposition") {
        // region Setup
        justRun {
            headerBasicManager.deleteComposition(compositionSourceId, authorId)
        }
        // endregion
        headersManager.deleteComposition(CompositionHeader.Basic, compositionSourceId, authorId)
    }
})
