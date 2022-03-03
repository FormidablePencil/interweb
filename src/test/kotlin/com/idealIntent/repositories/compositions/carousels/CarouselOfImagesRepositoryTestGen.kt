// =========
// Please do not make changes to this file directly. This is a generated file.
//
// This is a test of composition repository - CarouselOfImagesRepository.
// =========
package com.idealIntent.repositories.compositions.carousels

import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.services.CompositionService
import integrationTests.auth.flows.SignupFlow
import integrationTests.compositions.carousels.CarouselCompositionsFlow
import integrationTests.compositions.carousels.CarouselCompositionsFlow.Companion.carouselBasicImagesCreateReq
import io.kotest.assertions.failure
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import org.koin.core.component.inject
import shared.testUtils.BehaviorSpecUtRepo2
import shared.testUtils.rollback

public final class CarouselOfImagesRepositoryTestGen() : BehaviorSpecUtRepo2() {
    init {
        // region Setup
        val carouselOfImagesRepository: CarouselOfImagesRepository by inject()
        val compositionService: CompositionService by inject()
        val signupFlow: SignupFlow by inject()
        val carouselCompositionsFlow: CarouselCompositionsFlow by inject()

        suspend fun signup_then_createComposition(publicView: Boolean): Triple<Int, Int, Int> {
            val authorId = signupFlow.signupReturnId()
            val layoutId = compositionService.createNewLayout(
                name = CarouselCompositionsFlow.layoutName,
                authorId = authorId
            ).data ?: throw failure("Failed to get id of newly created layout.")

            val compositionSourceId = carouselCompositionsFlow.createComposition(
                publicView, layoutId,
                authorId
            )

            return Triple(compositionSourceId, layoutId, authorId)
        }

        beforeEach { clearAllMocks() }
        // endregion Setup


        given("getOnlyTopLvlIdsOfCompositionOnlyModifiable") {

            then("Author id not privileged to view nor modify. Failed to retrieve private composition") {
                rollback {
                    val (compositionSourceId, _, _) = signup_then_createComposition(true)

                    carouselOfImagesRepository.getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
                        compositionSourceId = compositionSourceId, authorId = 1
                    ) shouldBe null
                }
            }

            then("successfully retrieved private composition") {
                rollback {
                    val (compositionSourceId, _, authorId) = signup_then_createComposition(true)

                    carouselOfImagesRepository.getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
                        compositionSourceId = compositionSourceId, authorId = authorId
                    ) shouldNotBe null
                }
            }
        }

        given("getPublicComposition") {

            then("failed to get because composition is private") {
                rollback {
                    val (compositionSourceId, _, _) = signup_then_createComposition(false)

                    carouselOfImagesRepository.getPublicComposition(
                        compositionSourceId = compositionSourceId
                    ) shouldBe null
                }
            }

            then("successfully got public composition") {
                rollback {
                    val (compositionSourceId, _, _) = signup_then_createComposition(true)

                    val res: CarouselBasicImagesRes = carouselOfImagesRepository.getPublicComposition(
                        compositionSourceId = compositionSourceId
                    ) ?: throw failure("failed to get composition")

                    CarouselCompositionsFlow.validateDataResponse(res)
                }
            }
        }

        given("getPrivateComposition") {

            then("successfully got private composition") {
                rollback {
                    val (compositionSourceId, _, authorId) = signup_then_createComposition(false)

                    val res: CarouselBasicImagesRes = carouselOfImagesRepository.getPrivateComposition(
                        compositionSourceId, authorId
                    ) ?: throw failure("failed to get composition")

                    CarouselCompositionsFlow.validateDataResponse(res)
                }
            }
        }

        given("compose") {

            then("successfully composed collections and compositions as one composition") {
                rollback {
                    val (preparedComposition, sourceId) = carouselCompositionsFlow.prepareComposition(
                        res = carouselBasicImagesCreateReq
                    )

                    carouselOfImagesRepository.compose(preparedComposition, sourceId)
                }
            }
        }

        given("deleteComposition") {

            then("failed to delete because author id provided is not privileged to delete.") {
                rollback {
                    val (compositionSourceId, _, _) = signup_then_createComposition(true)

                    val ex = shouldThrowExactly<CompositionException> {
                        carouselOfImagesRepository.deleteComposition(compositionSourceId, 999999999)
                    }.code shouldBe CompositionCode.CompositionNotFound
                }
            }

            then("failed to delete on an id of a composition that doesn't exist") {
                rollback {
                    val (_, _, authorId) = signup_then_createComposition(true)

                    val ex = shouldThrowExactly<CompositionException> {
                        carouselOfImagesRepository.deleteComposition(999999999, authorId)
                    }.code shouldBe CompositionCode.CompositionNotFound
                }
            }

            then("success") {
                rollback {
                    val (compositionSourceId, _, authorId) = signup_then_createComposition(true)

                    // region before deletion assertions
                    val res: CarouselBasicImagesRes = carouselOfImagesRepository.getPublicComposition(
                        compositionSourceId = compositionSourceId
                    ) ?: throw failure("failed to get composition")

                    CarouselCompositionsFlow.validateDataResponse(res)
                    // endregion

                    carouselOfImagesRepository.deleteComposition(compositionSourceId, authorId)

                    // region after deletion assertions
                    val resAfterDeletion =
                        carouselOfImagesRepository.getPublicComposition(compositionSourceId)

                    resAfterDeletion shouldBe null
                    // endregion
                }
            }
        }
    }
}
