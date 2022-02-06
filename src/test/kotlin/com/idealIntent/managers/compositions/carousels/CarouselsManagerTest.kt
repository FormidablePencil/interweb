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
    val authorId = 1
    val gson = Gson()

    val carouselsManager = CarouselsManager(carouselOfImagesManager, carouselOfImagesRepository)

    beforeEach {
        clearAllMocks()
    }

    values().map {
        when (it) {
            BasicImages -> {
                given("getPublicComposition") {

                    then("provided id of composition that does NOT exist") {
                        every { carouselOfImagesRepository.getSingleCompositionOfPrivilegedAuthor(compositionId, authorId) } returns listOf()

                        carouselsManager.getComposition(it, compositionId, authorId) shouldBe null
                    }

                    then("success") {
                        every { carouselOfImagesRepository.getSingleCompositionOfPrivilegedAuthor(compositionId, authorId) } returns listOf()

                        carouselsManager.getComposition(it, compositionId, authorId) shouldBe carouselBasicImagesRes
                    }
                }
            }
        }
    }

    values().map {
        when (it) {
            BasicImages -> {
                given("createCompositionOfCategory - BasicImages") {
                    then("failed response") {
                        // region setup
                        every {
                            carouselOfImagesManager.createComposition(createCarouselBasicImagesReq, authorId)
                        } returns CompositionResponse().failed(CompositionCode.FailedToGivePrivilege)
                        // endregion setup

                        val res = carouselsManager.createCompositionOfCategory(
                            BasicImages, carouselBasicImagesReqStingified, authorId
                        )

                        res.data shouldBe null
                    }
                    then("success response") {
                        // region setup
                        val idOfNewlyCreatedComposition = 123
                        val httpStatus = HttpStatusCode.Created
                        every {
                            carouselOfImagesManager.createComposition(createCarouselBasicImagesReq, authorId)
                        } returns CompositionResponse().succeeded(httpStatus, idOfNewlyCreatedComposition)
                        // endregion setup

                        val res = carouselsManager.createCompositionOfCategory(
                            BasicImages, carouselBasicImagesReqStingified, authorId
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
