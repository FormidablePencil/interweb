package com.idealIntent.managers.compositions.carousels

import com.google.gson.Gson
import dtos.compositions.carousels.CompositionCarouselType.BasicImages
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import shared.testUtils.carouselBasicImagesRes
import shared.testUtils.carouselPublicBasicImagesReqSerialized
import shared.testUtils.createPublicCarouselBasicImagesReq

class CarouselsManagerTest : BehaviorSpec({
    val carouselOfImagesManager: CarouselOfImagesManager = mockk()
    val carouselBlurredOverlayManager: CarouselBlurredOverlayManager = mockk()
    val layoutId = 543
    val compositionSourceId = 1
    val authorId = 1
    val gson = Gson()

    val carouselsManager = CarouselsManager(carouselOfImagesManager, carouselBlurredOverlayManager)

    beforeEach {
        clearAllMocks()
    }

    given("getPublicComposition") {
        // region setup
        every {
            carouselOfImagesManager.getPublicComposition(
                compositionSourceId = compositionSourceId,
            )
        } returns carouselBasicImagesRes
        // endregion

        carouselsManager.getPublicComposition(
            compositionType = BasicImages,
            compositionSourceId = compositionSourceId,
        ) shouldBe carouselBasicImagesRes
    }

    given("getPrivateComposition") {
        // region setup
        every {
            carouselOfImagesManager.getPrivateComposition(
                compositionSourceId = compositionSourceId,
                authorId = authorId
            )
        } returns carouselBasicImagesRes
        // endregion

        carouselsManager.getPrivateComposition(
            compositionType = BasicImages,
            compositionSourceId = compositionSourceId,
            authorId = authorId
        ) shouldBe carouselBasicImagesRes
    }

    given("createComposition") {
        // region setup
        val idOfNewlyCreatedComposition = 123
        val httpStatus = HttpStatusCode.Created
        every {
            carouselOfImagesManager.createComposition(createPublicCarouselBasicImagesReq, layoutId, authorId)
        } returns idOfNewlyCreatedComposition
        // endregion setup

        val res = carouselsManager.createComposition(
            BasicImages, carouselPublicBasicImagesReqSerialized, layoutId, authorId
        )

        res shouldBe idOfNewlyCreatedComposition
    }

    given("updateComposition") {
        // region Setup
        justRun {
            carouselOfImagesManager.updateComposition(listOf(), compositionSourceId, authorId)
        }
        // endregion
        carouselsManager.updateComposition(BasicImages, compositionSourceId, listOf(), authorId)
    }

    given("deleteComposition") {
        // region Setup
        justRun {
            carouselOfImagesManager.deleteComposition(compositionSourceId, authorId)
        }
        // endregion
        carouselsManager.deleteComposition(BasicImages, compositionSourceId, authorId)
    }
})
