package com.idealIntent.managers.compositions.carousels

import com.google.gson.Gson
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.failed
import com.idealIntent.dtos.succeeded
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import dtos.compositions.carousels.CompositionCarousel.BasicImages
import dtos.compositions.carousels.CompositionCarousel.values
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
    val carouselOfImagesRepository: CarouselOfImagesRepository = mockk()
    val compositionId = 1
    val userId = 1
    val gson = Gson()

    val carouselsManager = CarouselsManager(carouselOfImagesManager, carouselOfImagesRepository)

    beforeEach {
        clearAllMocks()
        println("test that getComposition cleans before each test")
    }

    values().map {
        when (it) {
            BasicImages -> {
                given("getComposition") {

                    then("provided id of composition that does NOT exist") {
                        every { carouselOfImagesRepository.getComposition(compositionId) } returns null
                        carouselsManager.getComposition(it.value, compositionId) shouldBe null
                    }

                    then("provided id of composition that does") {
                        every { carouselOfImagesRepository.getComposition(compositionId) } returns carouselBasicImagesRes
                        carouselsManager.getComposition(it.value, compositionId) shouldBe carouselBasicImagesRes
                    }
                }
            }
        }
    }

    values().map {
        when (it) {
            BasicImages -> {
                given("createCompositionOfCategory - BasicImages") {
                    then("success") {
                        // region setup
                        val idOfNewlyCreatedComposition = 123
                        val httpStatus = HttpStatusCode.Created
                        every {
                            carouselOfImagesManager.createComposition(
                                createCarouselBasicImagesReq,
                                userId
                            )
                        } returns
                                CompositionResponse().succeeded(httpStatus, idOfNewlyCreatedComposition)
                        // endregion setup

                        val res =
                            carouselsManager.createCompositionOfCategory(
                                BasicImages.value, carouselBasicImagesReqStingified, userId
                            )

                        res.isSuccess shouldBe true
                        res.data shouldBe idOfNewlyCreatedComposition
                        res.successHttpStatusCode shouldBe httpStatus
                    }
                    then("failed") {
                        // region setup
                        every {
                            carouselOfImagesManager.createComposition(
                                createCarouselBasicImagesReq,
                                userId
                            )
                        } returns
                                CompositionResponse().failed(CompositionCode.FailedToGivePrivilege)
                        // endregion setup

                        val res = carouselsManager.createCompositionOfCategory(
                            BasicImages.value, carouselBasicImagesReqStingified, userId
                        )

                        res.data shouldBe null
                    }
                }
            }
        }
    }

    xgiven("updateComposition") { }

    xgiven("deleteComposition") { }
})
