package com.idealIntent.services

import com.idealIntent.dtos.compositions.UserComposition
import com.idealIntent.managers.SpaceManager
import com.idealIntent.managers.compositions.carousels.CarouselsManager
import com.idealIntent.managers.compositions.grids.GridsManager
import com.idealIntent.repositories.compositions.SpaceRepository
import dtos.compositions.CompositionCategory.*
import dtos.compositions.carousels.CompositionCarousel
import dtos.compositions.grids.CompositionGrid
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CompositionServiceTest : BehaviorSpec({
    val spaceRepository: SpaceRepository = mockk()
    val compositionService: CompositionService = mockk()
    val spaceManager: SpaceManager = mockk()
    val carouselsManager: CarouselsManager = mockk()
    val gridsManager: GridsManager = mockk()
    val name = "my very own layout"
    val layoutId = 54
    val compositionSourceId = 43
    val authorId = 545

    given("createComposition") {
        values().forEach {
            when (it) {
                Text -> {

                }
//                Markdown -> TODO() // link to mark down. todo move to text
                Banner -> {

                }
                Grid -> {
                    and("Grid") {
                        val userComposition = UserComposition(compositionCategory = it, 0)
                        then("created") {
                        }
                    }
                }
//                Divider -> TODO() // text and style todo move to text
//                LineDivider -> TODO() // style todo remove
                Carousel -> {
                    and("Carousel") {
                        then("created") {

                        }
                    }
                }
            }
        }
    }

    given("deleteComposition") {
        values().forEach {
            when (it) {
                Text -> TODO()
                Markdown -> TODO()
                Banner -> TODO()
                Grid -> {
                    and("Grid") {
                        val userComposition = UserComposition(compositionCategory = it, 0)
                        then("failed to delete") {
                            // region setup
                            every {
                                gridsManager.deleteComposition(
                                    CompositionGrid.fromInt(0), compositionSourceId, authorId
                                )
                            } returns false
                            // endregion

                            val res = compositionService.deleteComposition(
                                userComposition, compositionSourceId, authorId
                            )

                            res shouldBe false
                        }
                        then("deleted") {
                            // region setup
                            every {
                                gridsManager.deleteComposition(
                                    CompositionGrid.fromInt(0), compositionSourceId, authorId
                                )
                            } returns true
                            // endregion

                            val res = compositionService.deleteComposition(
                                userComposition, compositionSourceId, authorId
                            )

                            res shouldBe true
                        }
                    }
                }
                Divider -> TODO()
                LineDivider -> TODO()
                Carousel -> {
                    and("Carousel") {
                        val userComposition = UserComposition(compositionCategory = it, 0)
                        then("failed to delete") {
                            // region setup
                            every {
                                carouselsManager.deleteComposition(
                                    CompositionCarousel.fromInt(0), compositionSourceId, authorId
                                )
                            } returns false
                            // endregion

                            val res = compositionService.deleteComposition(
                                userComposition, compositionSourceId, authorId
                            )

                            res shouldBe false
                        }
                        then("deleted") {
                            // region setup
                            every {
                                carouselsManager.deleteComposition(
                                    CompositionCarousel.fromInt(0), compositionSourceId, authorId
                                )
                            } returns true
                            // endregion

                            val res = compositionService.deleteComposition(
                                userComposition, compositionSourceId, authorId
                            )

                            res shouldBe true
                        }
                    }
                }
            }
        }
    }

    given("createNewLayout") {
        then("success") {
            every { spaceRepository.insertNewLayout(name) } returns layoutId

            compositionService.createNewLayout(name)

            verify { spaceRepository.insertNewLayout(name) }
        }
    }
})
