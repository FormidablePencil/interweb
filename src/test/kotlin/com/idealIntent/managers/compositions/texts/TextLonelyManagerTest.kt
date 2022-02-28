package com.idealIntent.managers.compositions.texts

import com.idealIntent.dtos.compositions.texts.TextLonelyRes
import dtos.compositions.texts.CompositionTextType
import integrationTests.compositions.texts.TextCompositionsFlow.Companion.privateTextLonelyCreateReq
import integrationTests.compositions.texts.TextCompositionsFlow.Companion.publicTextLonelyCreateReq
import integrationTests.compositions.texts.TextCompositionsFlow.Companion.publicTextLonelyCreateReqSerialized
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk

class TextLonelyManagerTest : BehaviorSpec({
    val textLonelyManager: TextLonelyManager = mockk()
    val layoutId = 543
    val compositionSourceId = 1
    val authorId = 1

    val textLonelyRes = privateTextLonelyCreateReq.let {
        TextLonelyRes(
            compositionId = compositionSourceId,
            sourceId = 321,
            name = it.name,
            text = it.text,
            privilegeLevel = it.privilegeLevel,
            privilegedAuthors = it.privilegedAuthors,
        )
    }

    val textsManager = TextsManager(textLonelyManager)

    beforeEach { clearAllMocks() }

    given("getPublicComposition") {
        // region setup
        every {
            textLonelyManager.getPublicComposition(
                compositionSourceId = compositionSourceId,
            )
        } returns textLonelyRes
        // endregion

        textsManager.getPublicComposition(
            compositionType = CompositionTextType.Basic,
            compositionSourceId = compositionSourceId,
        ) shouldBe textLonelyRes
    }

    given("getPrivateComposition") {
        // region setup
        every {
            textLonelyManager.getPrivateComposition(
                compositionSourceId = compositionSourceId,
                authorId = authorId
            )
        } returns textLonelyRes
        // endregion

        textsManager.getPrivateComposition(
            compositionType = CompositionTextType.Basic,
            compositionSourceId = compositionSourceId,
            authorId = authorId
        ) shouldBe textLonelyRes
    }

    given("createComposition") {
        // region setup
        val idOfNewlyCreatedComposition = 123
        val httpStatus = HttpStatusCode.Created
        every {
            textLonelyManager.createComposition(publicTextLonelyCreateReq, layoutId, authorId)
        } returns idOfNewlyCreatedComposition
        // endregion setup

        val res = textsManager.createComposition(
            CompositionTextType.Basic, publicTextLonelyCreateReqSerialized, layoutId, authorId
        )

        res shouldBe idOfNewlyCreatedComposition
    }

    given("updateComposition") {
        // region Setup
        justRun {
            textLonelyManager.updateComposition(listOf(), compositionSourceId, authorId)
        }
        // endregion
        textsManager.updateComposition(CompositionTextType.Basic, compositionSourceId, listOf(), authorId)
    }

    given("deleteComposition") {
        // region Setup
        justRun {
            textLonelyManager.deleteComposition(compositionSourceId, authorId)
        }
        // endregion
        textsManager.deleteComposition(CompositionTextType.Basic, compositionSourceId, authorId)
    }
})
