package com.idealIntent.repositories.compositions.carousels

import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesCreateReq
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes
import com.idealIntent.dtos.compositions.carousels.CarouselOfImagesComposePrepared
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.services.CompositionService
import integrationTests.auth.flows.AuthUtilities
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

/**
 * Carousel of images repository integration/unit testing
 *
 * NOTE: Everything in reusable query instructions region is tested in composition integration tests
 */
class CarouselOfImagesRepositoryTest : BehaviorSpecUtRepo2() {
    init {
        val imageRepository: ImageRepository by inject()
        val textRepository: TextRepository by inject()
        val spaceRepository: SpaceRepository by inject()
        val carouselOfImagesRepository: CarouselOfImagesRepository by inject()
        val compositionPrivilegesManager: CompositionPrivilegesManager by inject()
        val compositionService: CompositionService by inject()
        val signupFlow: SignupFlow by inject()
        val carouselCompositionFlow: CarouselCompositionsFlow by inject()
//
//        val userComposition = NewUserComposition(
//            compositionCategory = CompositionCategory.Carousel,
//            compositionType = CompositionCarouselType.BasicImages.value,
//        )

        fun validateDataResponse(res: CarouselBasicImagesRes) {
            res.images.size shouldBe carouselBasicImagesCreateReq.images.size
            res.imgOnclickRedirects.size shouldBe carouselBasicImagesCreateReq.imgOnclickRedirects.size
            res.images.forEach { resItem ->
                carouselBasicImagesCreateReq.images.find {
                    it.orderRank == resItem.orderRank
                            && it.url == resItem.url
                            && it.description == resItem.description
                } shouldNotBe null
            }
            res.imgOnclickRedirects.forEach { item ->
                carouselBasicImagesCreateReq.imgOnclickRedirects.find {
                    item.orderRank == it.orderRank
                            && item.text == it.text
                } shouldNotBe null
            }
            res.name shouldBe carouselBasicImagesCreateReq.name
        }

        suspend fun signup_then_createComposition(publicView: Boolean): Triple<Int, Int, Int> {
            val authorId = signupFlow.signupReturnId(AuthUtilities.createAuthorRequest)
            val layoutId = compositionService.createNewLayout(
                name = CarouselCompositionsFlow.layoutName,
                authorId = authorId
            ).data ?: throw failure("Failed to get id of newly created layout.")
            val compositionSourceId = carouselCompositionFlow.createComposition(publicView, layoutId, authorId)
            return Triple(compositionSourceId, layoutId, authorId)
        }

        beforeEach { clearAllMocks() }

        given("getOnlyTopLvlIdsOfCompositionOnlyModifiable") {

            then("Author id not privileged to view nor modify. Failed to retrieve private composition") {
                rollback {
                    val (compositionSourceId, _, authorId) = signup_then_createComposition(true)

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
                    val (compositionSourceId, layoutId, authorId) =
                        signup_then_createComposition(false)

                    carouselOfImagesRepository.getPublicComposition(compositionSourceId = compositionSourceId) shouldBe null
                }
            }

            then("successfully got public composition") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) =
                        signup_then_createComposition(true)

                    val res: CarouselBasicImagesRes = carouselOfImagesRepository.getPublicComposition(
                        compositionSourceId = compositionSourceId
                    ) ?: throw failure("failed to get composition")

                    validateDataResponse(res)
                }
            }
        }

        given("getPrivateComposition") {

            then("successfully got private composition") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) =
                        signup_then_createComposition(false)

                    val res: CarouselBasicImagesRes = carouselOfImagesRepository.getPrivateComposition(
                        compositionSourceId, authorId
                    ) ?: throw failure("failed to get composition")

                    validateDataResponse(res)
                }
            }
        }

        given("compose") {
            suspend fun prepareComposition(): CarouselOfImagesComposePrepared {
                val authorId = signupFlow.signupReturnId()
                val createRequest = CarouselBasicImagesCreateReq(
                    name = "that was legitness",
                    images = listOf(),
                    imgOnclickRedirects = listOf(),
                    privilegedAuthors = listOf(),
                    privilegeLevel = 0,
                )
                val layoutId = spaceRepository.insertNewLayout(createRequest.name, authorId)

                val imageCollectionId = imageRepository.batchInsertRecordsToNewCollection(createRequest.images)
                val redirectsCollectionId =
                    textRepository.batchInsertRecordsToNewCollection(createRequest.imgOnclickRedirects)

                val compositionSourceId =
                    compositionPrivilegesManager.createCompositionSource(
                        compositionType = 0,
                        authorId = authorId,
                        name = "legit",
                        privilegeLevel = 0,
                    )

                spaceRepository.associateCompositionToLayout(
                    orderRank = 0,
                    compositionSourceId = compositionSourceId,
                    layoutId = layoutId
                )

                return CarouselOfImagesComposePrepared(
                    name = createRequest.name,
                    imageCollectionId = imageCollectionId,
                    redirectTextCollectionId = redirectsCollectionId,
                    sourceId = compositionSourceId,
                )
            }

            then("successfully composed collections and compositions as one composition") {
                rollback {
                    val preparedComposition = prepareComposition()

                    carouselOfImagesRepository.compose(
                        preparedComposition,
                        preparedComposition.sourceId
                    )
                }
            }
        }

        given("deleteComposition") {

            then("failed to delete because author id provided is not privileged to delete.") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) = signup_then_createComposition(true)

                    val ex = shouldThrowExactly<CompositionException> {
                        carouselOfImagesRepository.deleteComposition(compositionSourceId, 999999999)
                    }.code shouldBe CompositionCode.CompositionNotFound
                }
            }

            then("failed to delete on an id of a composition that doesn't exist") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) = signup_then_createComposition(true)

                    val ex = shouldThrowExactly<CompositionException> {
                        carouselOfImagesRepository.deleteComposition(999999999, authorId)
                    }.code shouldBe CompositionCode.CompositionNotFound
                }
            }

            then("success") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) = signup_then_createComposition(true)

                    // region before deletion assertions
                    val resBeforeDeletion: CarouselBasicImagesRes = carouselOfImagesRepository.getPublicComposition(
                        compositionSourceId = compositionSourceId
                    ) ?: throw failure("failed to get composition")

                    validateDataResponse(resBeforeDeletion)
                    // endregion

                    carouselOfImagesRepository.deleteComposition(compositionSourceId, authorId)

                    // region after deletion assertions
                    val resAfterDeletion = carouselOfImagesRepository.getPublicComposition(compositionSourceId)

                    resAfterDeletion shouldBe null
                    // endregion
                }
            }
        }
    }
}
