package com.idealIntent.services

import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.failed
import com.idealIntent.dtos.succeeded
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.managers.SpaceManager
import com.idealIntent.managers.compositions.carousels.CarouselsManager
import com.idealIntent.repositories.compositions.SpaceRepository
import dtos.compositions.CompositionCategory
import dtos.compositions.carousels.CompositionCarousel.BasicImages
import dtos.compositions.carousels.CompositionCarousel.values
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import shared.testUtils.carouselBasicImagesReqStingified

class CmsServiceTest : BehaviorSpec({
    val carouselsManager: CarouselsManager = mockk()
    val spaceManager: SpaceManager = mockk()
    val spaceRepository: SpaceRepository = mockk()
    val layoutId = 0
    val userId = 0

    val componentManager = CompositionService(carouselsManager, spaceManager, spaceRepository)

    beforeEach {
        clearAllMocks()
    }

    values().map {
        when (it) {
            BasicImages -> {
                given("createComposition BasicImages") {
                    then("successfully created composition") {
                        // region setup
                        val idOfNewlyCreatedComposition = 34
                        val httpStatus = HttpStatusCode.Created
                        every {
                            carouselsManager.createCompositionOfCategory(
                                BasicImages,
                                carouselBasicImagesReqStingified,
                                layoutId,
                                userId
                            )
                        } returns CompositionResponse().succeeded(httpStatus, idOfNewlyCreatedComposition)
                        // endregion setup

                        val res =
                            componentManager.createComposition(
                                CompositionCategory.Carousel,
                                BasicImages,
                                carouselBasicImagesReqStingified,
                                layoutId,
                                userId
                            )

                        res.isSuccess shouldBe true
                        res.data shouldBe idOfNewlyCreatedComposition
                        res.statusCode() shouldBe httpStatus
                        res.message() shouldBe null
                    }
                    then("failed to create composition") {
                        // region setup
                        every {
                            carouselsManager.createCompositionOfCategory(
                                BasicImages,
                                carouselBasicImagesReqStingified,
                                layoutId,
                                userId
                            )
                        } returns CompositionResponse().failed(CompositionCode.FailedToInsertRecord)
                        // endregion setup

                        val res =
                            componentManager.createComposition(
                                CompositionCategory.Carousel,
                                BasicImages,
                                carouselBasicImagesReqStingified,
                                layoutId, userId
                            )

                        res.isSuccess shouldBe false
                        res.data shouldBe null
                        res.code shouldBe CompositionCode.FailedToInsertRecord
                    }
                }
            }
        }
    }

    xgiven("...") {
//        val gson = Gson()
//
//        val image = Image(description = "image description", url = "image url", orderRank = 1, id = 1)
//        val navTo = Text(orderRank = 10000, text = "some link")
//        val privilegedAuthors = CompositionSourcesModel(authorId = 1, modLvl = 2)
//
//        val req = CreateCompositionRequest(
//            spaceAddress = "SDLFJEI",
//            userComposition = UserComposition(
//                compositionType = CompositionCategory.Carousel,
//                jsonData = gson.toJson(
//                    CarouselBasicImages(
//                        name = "project images",
//                        images = listOf(image),
//                        navToCorrespondingImagesOrder = listOf(navTo),
//                        privilegedAuthors = listOf(privilegedAuthors)
//                    )
//                )
//            ),
//        )
////        val res = componentManager.createComposition(req.userComposition, req.spaceAddress)
    }

    xgiven("deleteComposition") { }
})