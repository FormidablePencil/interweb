package com.idealIntent.services

import com.idealIntent.dtos.collectionsGeneric.texts.Text
import com.idealIntent.dtos.compositionCRUD.SingleUpdateCompositionRequest
import com.idealIntent.dtos.compositions.ExistingUserComposition
import com.idealIntent.dtos.compositions.NewUserComposition
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.failed
import com.idealIntent.dtos.succeeded
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.managers.SpaceManager
import com.idealIntent.managers.compositions.banners.BannersManager
import com.idealIntent.managers.compositions.carousels.CarouselsManager
import com.idealIntent.managers.compositions.grids.GridsManager
import com.idealIntent.managers.compositions.texts.TextsManager
import com.idealIntent.repositories.compositions.SpaceRepository
import dtos.compositions.CompositionCategory
import dtos.compositions.banners.CompositionBanner
import dtos.compositions.carousels.CompositionCarouselType
import dtos.compositions.grids.CompositionGrid
import dtos.compositions.texts.CompositionText
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import shared.testUtils.carouselPublicBasicImagesReqSerialized

/**
 * Composition service test
 *
 * todo - convert test to kotlin poet.
 */
class CompositionServiceTest : BehaviorSpec({
    val spaceManager: SpaceManager = mockk()
    val spaceRepository: SpaceRepository = mockk()
    val carouselsManager: CarouselsManager = mockk()
    val textsManager: TextsManager = mockk()
    val bannersManager: BannersManager = mockk()
    val gridsManager: GridsManager = mockk()
    val layoutId = 0
    val authorId = 0
    val name = "my very own layout"

    val compositionService = CompositionService(
        carouselsManager, spaceManager, spaceRepository, textsManager, bannersManager, gridsManager,
    )

    beforeEach { clearAllMocks() }

    fun genNewUserComposition(compositionCategory: CompositionCategory) = NewUserComposition(
        compositionCategory = compositionCategory,
        compositionType = 0,
    )

    fun genExistingUserComposition(compositionCategory: CompositionCategory) = ExistingUserComposition(
        compositionSourceId = 432,
        compositionCategory = compositionCategory,
        compositionType = 0,
    )

    fun genSingleUpdateReq(compositionCategory: CompositionCategory): SingleUpdateCompositionRequest =
        SingleUpdateCompositionRequest(
            compositionSourceId = 432,
            compositionCategory = compositionCategory,
            compositionType = 0,
            compositionUpdateQue = listOf(),
            authorId = authorId,
        )

    // region only really need one
//    given("Text") {
//
//        and("createComposition") {
//            val newUserComposition = genNewUserComposition(CompositionCategory.Text)
//
//            then("successfully created composition") {
//                // region setup
//                val idOfNewlyCreatedComposition = 34
//                val httpStatus = HttpStatusCode.Created
//                every {
//                    textsManager.createComposition(
//                        compositionType = CompositionText.fromInt(newUserComposition.compositionType),
//                        jsonData = carouselPublicBasicImagesReqSerialized,
//                        layoutId = layoutId,
//                        userId = authorId
//
//                    )
//                } returns idOfNewlyCreatedComposition
//                // endregion setup
//
//                val res = compositionService.createComposition(
//                    userComposition = newUserComposition,
//                    jsonData = carouselPublicBasicImagesReqSerialized,
//                    layoutId = layoutId,
//                    userId = authorId
//                )
//
//                res.isSuccess shouldBe true
//                res.data shouldBe idOfNewlyCreatedComposition
//                res.statusCode() shouldBe httpStatus
//                res.message() shouldBe null
//            }
//
//            then("failed to create composition") {
//                // region setup
//                every {
//                    textsManager.createComposition(
//                        compositionType = CompositionText.fromInt(newUserComposition.compositionType),
//                        jsonData = carouselPublicBasicImagesReqSerialized,
//                        layoutId = layoutId,
//                        userId = authorId
//                    )
//                } throws CompositionException(CompositionCode.FailedToInsertRecord)
//                // endregion setup
//
//                val res = compositionService.createComposition(
//                    userComposition = newUserComposition,
//                    jsonData = carouselPublicBasicImagesReqSerialized,
//                    layoutId = layoutId,
//                    userId = authorId
//                )
//
//                res.isSuccess shouldBe false
//                res.data shouldBe null
//                res.code shouldBe CompositionCode.FailedToInsertRecord
//            }
//        }
//
//        and("deleteComposition") {
//            val userComposition = genExistingUserComposition(CompositionCategory.Text)
//
//            then("failed to delete") {
//                // region setup
//                every {
//                    textsManager.deleteComposition(
//                        CompositionText.fromInt(userComposition.compositionType),
//                        userComposition.compositionSourceId,
//                        authorId = authorId
//                    )
//                }
//                // endregion
//
//                val res = compositionService.deleteComposition(userComposition, authorId)
//
//                res shouldBe false
//            }
//
//            then("deleted") {
//                // region setup
//                every {
//                    textsManager.deleteComposition(
//                        CompositionText.fromInt(userComposition.compositionType),
//                        userComposition.compositionSourceId,
//                        authorId
//                    )
//                }
//                // endregion
//
//                val res = compositionService.deleteComposition(userComposition, authorId)
//                res shouldBe true
//            }
//        }
//
//        and("updateComposition") {
//            val request = genSingleUpdateReq(CompositionCategory.Text)
//
//            then("failed to update") {
//                // region setup
//                every {
//                    textsManager.updateComposition(
//                        CompositionText.fromInt(request.compositionType),
//                        request.compositionSourceId,
//                        compositionUpdateQue = request.compositionUpdateQue,
//                        authorId = authorId
//                    )
//                } throws Exception()
//                // endregion
//
//                val res = compositionService.updateComposition(request)
//
//            }
//            then("updated") {
//                // region setup
//                justRun {
//                    textsManager.updateComposition(
//                        compositionType = CompositionText.fromInt(request.compositionType),
//                        compositionSourceId = request.compositionSourceId,
//                        compositionUpdateQue = request.compositionUpdateQue,
//                        authorId = authorId
//                    )
//                }
//                // endregion
//
//                val res = compositionService.updateComposition(request)
//            }
//        }
//    }
//
//    given("Banner") {
//
//        and("createComposition") {
//            TODO()
//            val newUserComposition = genNewUserComposition(CompositionCategory.Banner)
//
//            then("successfully created composition") {
////                // region setup
////                val idOfNewlyCreatedComposition = 34
////                val httpStatus = HttpStatusCode.Created
////                every {
////                    bannersManager.createComposition(
////                        compositionType = CompositionBanner.fromInt(newUserComposition.compositionType),
////                        jsonData = carouselPublicBasicImagesReqStingified,
////                        layoutId = layoutId,
////                        userId = authorId
////
////                    )
////                } returns CompositionResponse().succeeded(httpStatus, idOfNewlyCreatedComposition)
////                // endregion setup
////
////                val res = compositionService.createComposition(
////                    userComposition = newUserComposition,
////                    jsonData = carouselPublicBasicImagesReqStingified,
////                    layoutId = layoutId,
////                    userId = authorId
////                )
////
////                res.isSuccess shouldBe true
////                res.data shouldBe idOfNewlyCreatedComposition
////                res.statusCode() shouldBe httpStatus
////                res.message() shouldBe null
//            }
//
//            then("failed to create composition") {
//                TODO()
////                // region setup
////                every {
////                    bannersManager.createComposition(
////                        compositionType = CompositionBanner.fromInt(newUserComposition.compositionType),
////                        jsonData = carouselPublicBasicImagesReqStingified,
////                        layoutId = layoutId,
////                        userId = authorId
////                    )
////                } returns CompositionResponse().failed(CompositionCode.FailedToInsertRecord)
////                // endregion setup
////
////                val res = compositionService.createComposition(
////                    userComposition = newUserComposition,
////                    jsonData = carouselPublicBasicImagesReqStingified,
////                    layoutId = layoutId,
////                    userId = authorId
////                )
////
////                res.isSuccess shouldBe false
////                res.data shouldBe null
////                res.code shouldBe CompositionCode.FailedToInsertRecord
//            }
//        }
//
//        and("deleteComposition") {
//            TODO()
////            val userComposition = genExistingUserComposition(Banner)
////
////            then("failed to delete") {
////                // region setup
////                every {
////                    bannersManager.deleteComposition(
////                        CompositionBanner.fromInt(userComposition.compositionType),
////                        userComposition.compositionSourceId,
////                        authorId = authorId
////                    )
////                } returns false
////                // endregion
////
////                val res = compositionService.deleteComposition(userComposition, authorId)
////
////                res shouldBe false
////            }
////
////            then("deleted") {
////                // region setup
////                every {
////                    bannersManager.deleteComposition(
////                        CompositionBanner.fromInt(userComposition.compositionType),
////                        userComposition.compositionSourceId,
////                        authorId
////                    )
////                } returns true
////                // endregion
////
////                val res = compositionService.deleteComposition(userComposition, authorId)
////                res shouldBe true
////            }
//        }
//
//        and("updateComposition") {
//            val request = genSingleUpdateReq(CompositionCategory.Banner)
//
//            then("failed to update") {
//                // region setup
//                every {
//                    bannersManager.updateComposition(
//                        CompositionBanner.fromInt(request.compositionType),
//                        request.compositionSourceId,
//                        compositionUpdateQue = request.compositionUpdateQue,
//                        authorId = authorId
//                    )
//                } throws Exception()
//                // endregion
//
//                val res = compositionService.updateComposition(request)
//
//            }
//
//            then("updated") {
//                // region setup
//                justRun {
//                    bannersManager.updateComposition(
//                        compositionType = CompositionBanner.fromInt(request.compositionType),
//                        compositionSourceId = request.compositionSourceId,
//                        compositionUpdateQue = request.compositionUpdateQue,
//                        authorId = authorId
//                    )
//                }
//                // endregion
//
//                val res = compositionService.updateComposition(request)
//            }
//        }
//    }
//
//    given("Grid") {
//
//        and("createComposition") {
//            val newUserComposition = genNewUserComposition(CompositionCategory.Grid)
//
//            then("successfully created composition") {
//                // region setup
//                val idOfNewlyCreatedComposition = 34
//                val httpStatus = HttpStatusCode.Created
//                every {
//                    gridsManager.createComposition(
//                        compositionType = CompositionGrid.fromInt(newUserComposition.compositionType),
//                        jsonData = carouselPublicBasicImagesReqSerialized,
//                        layoutId = layoutId,
//                        userId = authorId,
//                    )
//                } returns idOfNewlyCreatedComposition
//                // endregion setup
//
//                val res = compositionService.createComposition(
//                    userComposition = newUserComposition,
//                    jsonData = carouselPublicBasicImagesReqSerialized,
//                    layoutId = layoutId,
//                    userId = authorId,
//                )
//
//                res.isSuccess shouldBe true
//                res.data shouldBe idOfNewlyCreatedComposition
//                res.statusCode() shouldBe httpStatus
//                res.message() shouldBe null
//            }
//
//            then("failed to create composition") {
//                // region setup
//                every {
//                    gridsManager.createComposition(
//                        compositionType = CompositionGrid.fromInt(newUserComposition.compositionType),
//                        jsonData = carouselPublicBasicImagesReqSerialized,
//                        layoutId = layoutId,
//                        userId = authorId
//                    )
//                } throws CompositionException(CompositionCode.FailedToInsertRecord)
//                // endregion setup
//
//                val res = compositionService.createComposition(
//                    userComposition = newUserComposition,
//                    jsonData = carouselPublicBasicImagesReqSerialized,
//                    layoutId = layoutId,
//                    userId = authorId
//                )
//
//                res.isSuccess shouldBe false
//                res.data shouldBe null
//                res.code shouldBe CompositionCode.FailedToInsertRecord
//            }
//        }
//
//        and("deleteComposition") {
//            val userComposition = genExistingUserComposition(CompositionCategory.Grid)
//
//            then("failed to delete") {
//                // region setup
//                every {
//                    gridsManager.deleteComposition(
//                        CompositionGrid.fromInt(userComposition.compositionType),
//                        userComposition.compositionSourceId,
//                        authorId
//                    )
//                }
//                // endregion
//
//                val res = compositionService.deleteComposition(userComposition, authorId)
//
//                res shouldBe false
//            }
//
//            then("deleted") {
//                // region setup
//                every {
//                    gridsManager.deleteComposition(
//                        CompositionGrid.fromInt(userComposition.compositionType),
//                        userComposition.compositionSourceId,
//                        authorId
//                    )
//                }
//                // endregion
//
//                val res = compositionService.deleteComposition(userComposition, authorId)
//
//                res shouldBe true
//            }
//        }
//
//        and("updateComposition") {
//            val request = genSingleUpdateReq(CompositionCategory.Grid)
//
//            then("failed to update") {
//                // region setup
//                every {
//                    gridsManager.updateComposition(
//                        CompositionGrid.fromInt(request.compositionType),
//                        request.compositionSourceId,
//                        compositionUpdateQue = request.compositionUpdateQue,
//                        authorId = authorId
//                    )
//                } throws Exception()
//                // endregion
//
//                val res = compositionService.updateComposition(request)
//
//            }
//            then("updated") {
//                // region setup
//                justRun {
//                    gridsManager.updateComposition(
//                        compositionType = CompositionGrid.fromInt(request.compositionType),
//                        compositionSourceId = request.compositionSourceId,
//                        compositionUpdateQue = request.compositionUpdateQue,
//                        authorId = authorId
//                    )
//                }
//                // endregion
//
//                val res = compositionService.updateComposition(request)
//            }
//        }
//    }
    // endregion

    given("Some category - Carousel") {

        and("createComposition") {
            val newUserComposition = genNewUserComposition(CompositionCategory.Carousel)

            then("successfully created composition") {
                // region setup
                val idOfNewlyCreatedComposition = 34
                val httpStatus = HttpStatusCode.Created
                every {
                    carouselsManager.createComposition(
                        compositionType = CompositionCarouselType.fromInt(newUserComposition.compositionType),
                        jsonData = carouselPublicBasicImagesReqSerialized,
                        layoutId = layoutId,
                        userId = authorId
                    )
                } returns idOfNewlyCreatedComposition
                // endregion setup

                val res = compositionService.createComposition(
                    userComposition = newUserComposition,
                    jsonData = carouselPublicBasicImagesReqSerialized,
                    layoutId = layoutId,
                    userId = authorId
                )

                res.isSuccess shouldBe true
                res.data shouldBe idOfNewlyCreatedComposition
                res.statusCode() shouldBe httpStatus
                res.message() shouldBe null
            }

            then("failed to create composition") {
                // region setup
                every {
                    carouselsManager.createComposition(
                        compositionType = CompositionCarouselType.fromInt(newUserComposition.compositionType),
                        jsonData = carouselPublicBasicImagesReqSerialized,
                        layoutId = layoutId,
                        userId = authorId
                    )
                } throws CompositionException(CompositionCode.FailedToInsertRecord)
                // endregion setup

                val res = compositionService.createComposition(
                    userComposition = newUserComposition,
                    jsonData = carouselPublicBasicImagesReqSerialized,
                    layoutId = layoutId,
                    userId = authorId
                )

                res.isSuccess shouldBe false
                res.data shouldBe null
                res.code shouldBe CompositionCode.FailedToInsertRecord
            }
        }

        and("deleteComposition") {
            val userComposition = genExistingUserComposition(CompositionCategory.Carousel)

            then("failed to delete") {
                // region setup
                every {
                    carouselsManager.deleteComposition(
                        CompositionCarouselType.fromInt(userComposition.compositionType),
                        userComposition.compositionSourceId,
                        authorId = authorId
                    )
                }
                // endregion

                val res = compositionService.deleteComposition(userComposition, authorId)

                res shouldBe false
            }

            then("deleted") {
                // region setup
                every {
                    carouselsManager.deleteComposition(
                        CompositionCarouselType.fromInt(userComposition.compositionType),
                        userComposition.compositionSourceId,
                        authorId
                    )
                }
                // endregion

                val res = compositionService.deleteComposition(userComposition, authorId)
                res shouldBe true
            }
        }

        and("updateComposition") {
            val request = genSingleUpdateReq(CompositionCategory.Carousel)

            then("failed to update") {
                // region setup
                every {
                    carouselsManager.updateComposition(
                        CompositionCarouselType.fromInt(request.compositionType),
                        request.compositionSourceId,
                        compositionUpdateQue = request.compositionUpdateQue,
                        authorId = authorId
                    )
                } throws Exception()
                // endregion

                val res = compositionService.updateComposition(request)

            }

            then("updated") {
                // region setup
                justRun {
                    carouselsManager.updateComposition(
                        compositionType = CompositionCarouselType.fromInt(request.compositionType),
                        compositionSourceId = request.compositionSourceId,
                        compositionUpdateQue = request.compositionUpdateQue,
                        authorId = authorId
                    )
                }
                // endregion

                val res = compositionService.updateComposition(request)
            }
        }
    }

    given("createNewLayout") {

        then("success") {
            TODO()
//                every { spaceRepository.insertNewLayout(name) } returns layoutId
//
//                compositionService.createNewLayout(name)
//
//                verify { spaceRepository.insertNewLayout(name) }
        }
    }
})