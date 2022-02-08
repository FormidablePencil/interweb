package com.idealIntent.repositories.compositions.carousels

import com.idealIntent.configurations.DIHelper
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes
import com.idealIntent.dtos.compositions.carousels.CreateCarouselBasicImagesReq
import com.idealIntent.managers.compositions.carousels.CarouselOfImagesManager
import com.idealIntent.repositories.compositions.SpaceRepository
import integrationTests.auth.flows.AuthUtilities
import integrationTests.auth.flows.SignupFlow
import io.kotest.assertions.failure
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

class CarouselOfImagesRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(listOf(DIHelper.CoreModule, DITestHelper.FlowModule)))
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest
    private val carouselOfImagesRepository: CarouselOfImagesRepository by inject()
    private val carouselOfImagesManager: CarouselOfImagesManager by inject()
    private val spaceRepository: SpaceRepository by inject()
    private val signupFlow: SignupFlow by inject()

    private val createCarouselBasicImagesReq =
        CreateCarouselBasicImagesReq("Projects", images, texts, listOf())


    private suspend fun createAccountAndCompositionAndGetIds(): Pair<Int, Int> {
        val authorId = signupFlow.signupReturnId(AuthUtilities.createAuthorRequest)
        val layoutId = spaceRepository.insertNewLayout(name = "That was lagitness")
        // todo create a flow for this and use CmsService.createComposition instead
        val res = carouselOfImagesManager.createComposition(createCarouselBasicImagesReq, layoutId, authorId)
        res.isSuccess shouldBe true
        res.data ?: throw failure("failed to return composition id at setup")
        return Pair(res.data!!, authorId)
    }

    init {
        beforeEach { clearAllMocks() }

        given("Composition query instructions - Create a few compositions") {
            and("save under layout and query layout of compositions") {
                then("success") {

                }
            }
        }

        given("getPublicComposition") {
            then("success") {
                rollback {
                    val compositionId = createAccountAndCompositionAndGetIds()
                }
            }
        }

        given("getPrivateComposition") {
            then("success") {
            }
        }

        given("compose") {
            then("success") {
                rollback {

                }
            }
        }

        given("deleteComposition") {
            then("success") {
                val (authorId, compositionSourceId) = createAccountAndCompositionAndGetIds()

                // region before deletion assertion
                // todo - test privileges also
                val resBeforeDeletion: CarouselBasicImagesRes =
                    carouselOfImagesRepository.getPublicComposition(compositionSourceId)

                resBeforeDeletion.images.size shouldBe createCarouselBasicImagesReq.images.size
                resBeforeDeletion.imgOnclickRedirects.size shouldBe createCarouselBasicImagesReq.imgOnclickRedirects.size
                resBeforeDeletion.images.forEach { resItem ->
                    val found = createCarouselBasicImagesReq.images.find { it.orderRank == resItem.orderRank }
                    found shouldNotBe null
                }
                resBeforeDeletion.imgOnclickRedirects.forEach { item ->
                    val found = createCarouselBasicImagesReq.imgOnclickRedirects.find { item.orderRank == it.orderRank }
                    found shouldNotBe null
                }
                resBeforeDeletion.name shouldBe createCarouselBasicImagesReq.name
                // endregion

                carouselOfImagesRepository.deleteComposition(compositionSourceId, authorId)

                // region after deletion assertion
                val resAfterDeletion: CarouselBasicImagesRes =
                    carouselOfImagesRepository.getPublicComposition(compositionSourceId)

                resAfterDeletion.images.size shouldBe 0
                resAfterDeletion.imgOnclickRedirects.size shouldBe 0
                resAfterDeletion.images.forEach { item ->
                    val found = createCarouselBasicImagesReq.images.find { it.orderRank == item.orderRank }
                    found shouldNotBe null
                }
                resBeforeDeletion.imgOnclickRedirects.forEach { item ->
                    val found = createCarouselBasicImagesReq.imgOnclickRedirects.find { item.orderRank == it.orderRank }
                    found shouldNotBe null
                }
                resBeforeDeletion.name shouldBe createCarouselBasicImagesReq.name
                // endregion
            }
        }
    }
}
