package com.idealIntent.repositories.compositions.carousels

import com.google.gson.Gson
import com.idealIntent.configurations.DIHelper
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes
import com.idealIntent.dtos.compositions.carousels.CreateCarouselBasicImagesReq
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.SpaceRepository
import integrationTests.auth.flows.SignupFlow
import integrationTests.compositions.flows.CompositionFlow
import io.kotest.assertions.failure
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.IsolationMode
import io.kotest.koin.KoinListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import org.koin.core.component.inject
import shared.DITestHelper
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.images
import shared.testUtils.rollback
import shared.testUtils.texts

/**
 * Carousel of images repository integration/unit testing
 *
 * NOTE: Everything in reusable query instructions region is tested in composition integration tests
 */
class CarouselOfImagesRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(listOf(DIHelper.CoreModule, DITestHelper.FlowModule)))
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest
    private val imageRepository: ImageRepository by inject()
    private val textRepository: TextRepository by inject()
    private val spaceRepository: SpaceRepository by inject()
    private val carouselOfImagesRepository: CarouselOfImagesRepository by inject()
    private val compositionPrivilegesManager: CompositionPrivilegesManager by inject()
    private val signupFlow: SignupFlow by inject()
    private val compositionFlow: CompositionFlow by inject()

    private val createCarouselBasicImagesReq =
        CreateCarouselBasicImagesReq("Projects", images, texts, listOf(), privilegeLevel = 0)

    init {
        beforeEach { clearAllMocks() }

        given("getOnlyTopLvlIdsOfCompositionOnlyModifiable") {

            then("Author id not privileged to view nor modify. Failed to retrieve private composition") {
                rollback {
                    val (compositionSourceId, _, authorId) = compositionFlow.signup_then_createComposition(true)

                    carouselOfImagesRepository.getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
                        compositionSourceId = compositionSourceId, authorId = 1
                    ) shouldBe null
                }
            }

            then("successfully retrieved private composition") {
                rollback {
                    val (compositionSourceId, _, authorId) = compositionFlow.signup_then_createComposition(true)

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
                        compositionFlow.signup_then_createComposition(false)

                    carouselOfImagesRepository.getPublicComposition(compositionSourceId = compositionSourceId) shouldBe null
                }
            }

            then("successfully got public composition") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) =
                        compositionFlow.signup_then_createComposition(true)

                    val comp: CarouselBasicImagesRes = carouselOfImagesRepository.getPublicComposition(
                        compositionSourceId = compositionSourceId
                    ) ?: throw failure("failed to get composition")

                    // region verify
                    comp.images.size shouldBe createCarouselBasicImagesReq.images.size
                    comp.imgOnclickRedirects.size shouldBe createCarouselBasicImagesReq.imgOnclickRedirects.size
                    comp.images.forEach { resItem ->
                        createCarouselBasicImagesReq.images.find {
                            it.orderRank == resItem.orderRank
                                    && it.description == resItem.description
                                    && it.url == resItem.url
                        } shouldNotBe null
                    }
                    comp.imgOnclickRedirects.forEach { item ->
                        createCarouselBasicImagesReq.imgOnclickRedirects.find {
                            it.text == item.text
                                    && it.orderRank == item.orderRank
                        } shouldNotBe null
                    }
                    comp.name shouldBe createCarouselBasicImagesReq.name
                    // endregion
                }
            }
        }

        given("getPrivateComposition") {

            then("successfully got private composition") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) =
                        compositionFlow.signup_then_createComposition(false)

                    val comp: CarouselBasicImagesRes =
                        carouselOfImagesRepository.getPrivateComposition(compositionSourceId, authorId)
                            ?: throw failure("failed to get composition")

                    // region verify
                    comp.images.size shouldBe createCarouselBasicImagesReq.images.size
                    comp.imgOnclickRedirects.size shouldBe createCarouselBasicImagesReq.imgOnclickRedirects.size
                    comp.images.forEach { resItem ->
                        createCarouselBasicImagesReq.images.find {
                            it.orderRank == resItem.orderRank
                                    && it.description == resItem.description
                                    && it.url == resItem.url
                        } shouldNotBe null
                    }
                    comp.imgOnclickRedirects.forEach { item ->
                        createCarouselBasicImagesReq.imgOnclickRedirects.find {
                            it.text == item.text
                                    && it.orderRank == item.orderRank
                        } shouldNotBe null
                    }
                    comp.name shouldBe createCarouselBasicImagesReq.name
                    // endregion
                }
            }
        }

        given("compose") {
            suspend fun prepareComposition(): CarouselOfImagesComposePrepared {
                val authorId = signupFlow.signupReturnId()
                val createRequest = CreateCarouselBasicImagesReq(
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

                // todo wrap in a try catch and response to user that layout by id does not exist
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
                        CarouselOfImagesComposePrepared(
                            name = preparedComposition.name,
                            imageCollectionId = preparedComposition.imageCollectionId,
                            redirectTextCollectionId = preparedComposition.redirectTextCollectionId,
                            sourceId = preparedComposition.sourceId,
                        )
                    )
                }
            }
        }

        given("deleteComposition") {

            then("failed to delete because author id provided is not privileged to delete.") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) =
                        compositionFlow.signup_then_createComposition(true)

                    val ex = shouldThrowExactly<CompositionException> {
                        carouselOfImagesRepository.deleteComposition(compositionSourceId, 999999999)
                    }
                    ex.code shouldBe CompositionCode.CompositionNotFound
                }
            }

            then("failed to delete on an id of a composition that doesn't exist") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) =
                        compositionFlow.signup_then_createComposition(true)

                    val ex = shouldThrowExactly<CompositionException> {
                        carouselOfImagesRepository.deleteComposition(999999999, authorId)
                    }
                    ex.code shouldBe CompositionCode.CompositionNotFound
                }
            }

            then("success") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) =
                        compositionFlow.signup_then_createComposition(true)

                    // region before deletion assertions
                    val resBeforeDeletion: CarouselBasicImagesRes = carouselOfImagesRepository.getPublicComposition(
                        compositionSourceId = compositionSourceId
                    ) ?: throw failure("failed to get composition")

                    resBeforeDeletion.images.size shouldBe createCarouselBasicImagesReq.images.size
                    resBeforeDeletion.imgOnclickRedirects.size shouldBe createCarouselBasicImagesReq.imgOnclickRedirects.size
                    resBeforeDeletion.images.forEach { resItem ->
                        createCarouselBasicImagesReq.images.find {
                            it.orderRank == resItem.orderRank
                                    && it.url == resItem.url
                                    && it.description == resItem.description
                        } shouldNotBe null
                    }
                    resBeforeDeletion.imgOnclickRedirects.forEach { item ->
                        createCarouselBasicImagesReq.imgOnclickRedirects.find {
                            item.orderRank == it.orderRank
                                    && item.text == it.text
                        } shouldNotBe null
                    }
                    resBeforeDeletion.name shouldBe createCarouselBasicImagesReq.name
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
