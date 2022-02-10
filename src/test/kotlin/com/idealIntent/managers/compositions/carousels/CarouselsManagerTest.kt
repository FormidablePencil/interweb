package com.idealIntent.managers.compositions.carousels

import com.google.gson.Gson
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.failed
import com.idealIntent.dtos.succeeded
import com.idealIntent.exceptions.CompositionCode
import dtos.compositions.carousels.CompositionCarouselType.*
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import shared.testUtils.carouselBasicImagesReqStingified
import shared.testUtils.carouselBasicImagesRes
import shared.testUtils.createCarouselBasicImagesReq

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
        values().map {
            when (it) {
                CarouselBlurredOverlay -> {
                    then("provided id of composition that does NOT exist") {
                        // region setup
                        every {
                            carouselsManager.getPrivateComposition(
                                compositionType = it,
                                compositionSourceId = compositionSourceId,
                                authorId = authorId
                            )
                        } returns null
                        // endregion

                        carouselsManager.getPrivateComposition(
                            compositionType = it,
                            compositionSourceId = compositionSourceId,
                            authorId = authorId
                        ) shouldBe null
                    }

                    then("success") {
                        // region setup
                        every {
                            carouselsManager.getPrivateComposition(
                                compositionType = it,
                                compositionSourceId = compositionSourceId,
                                authorId = authorId
                            )
                        } returns carouselBasicImagesRes
                        // endregion

                        carouselsManager.getPrivateComposition(
                            compositionType = it,
                            compositionSourceId = compositionSourceId,
                            authorId = authorId
                        ) shouldBe carouselBasicImagesRes
                    }
                }
                BasicImages -> {
                    then("provided id of composition that does NOT exist") {
                        // region setup
                        every {
                            carouselOfImagesManager.getPrivateComposition(
                                compositionSourceId = compositionSourceId,
                                authorId = authorId
                            )
                        } returns null
                        // endregion

                        carouselsManager.getPrivateComposition(
                            compositionType = it,
                            compositionSourceId = compositionSourceId,
                            authorId = authorId
                        ) shouldBe null
                    }

                    then("success") {
                        // region setup
                        every {
                            carouselOfImagesManager.getPrivateComposition(
                                compositionSourceId = compositionSourceId,
                                authorId = authorId
                            )
                        } returns carouselBasicImagesRes
                        // endregion

                        carouselsManager.getPrivateComposition(
                            compositionType = it,
                            compositionSourceId = compositionSourceId,
                            authorId = authorId
                        ) shouldBe carouselBasicImagesRes
                    }
                }
            }
        }
    }

    given("createCompositionOfCategory") {
        values().map {
            when (it) {
                CarouselBlurredOverlay -> {

                }

                BasicImages -> {
                    then("failed response") {
                        // region setup
                        every {
                            carouselOfImagesManager.createComposition(createCarouselBasicImagesReq, layoutId, authorId)
                        } returns CompositionResponse().failed(CompositionCode.FailedToGivePrivilege)
                        // endregion setup

                        val res = carouselsManager.createComposition(
                            BasicImages, carouselBasicImagesReqStingified, layoutId, authorId
                        )

                        res.data shouldBe null
                    }
                    then("success response") {
                        // region setup
                        val idOfNewlyCreatedComposition = 123
                        val httpStatus = HttpStatusCode.Created
                        every {
                            carouselOfImagesManager.createComposition(createCarouselBasicImagesReq, layoutId, authorId)
                        } returns CompositionResponse().succeeded(httpStatus, idOfNewlyCreatedComposition)
                        // endregion setup

                        val res = carouselsManager.createComposition(
                            BasicImages, carouselBasicImagesReqStingified, layoutId, authorId
                        )

                        res.isSuccess shouldBe true
                        res.data shouldBe idOfNewlyCreatedComposition
                        res.successHttpStatusCode shouldBe httpStatus
                    }
                }
            }
        }
    }

    xgiven("updateComposition") { }

    xgiven("deleteComposition") { }
})
