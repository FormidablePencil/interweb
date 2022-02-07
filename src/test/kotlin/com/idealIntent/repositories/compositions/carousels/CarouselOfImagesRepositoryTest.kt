package com.idealIntent.repositories.compositions.carousels

import com.idealIntent.configurations.DIHelper
import com.idealIntent.dtos.compositions.carousels.CreateCarouselBasicImagesReq
import com.idealIntent.managers.compositions.carousels.CarouselOfImagesManager
import com.idealIntent.repositories.compositions.SpaceRepository
import integrationTests.auth.flows.AuthUtilities
import integrationTests.auth.flows.SignupFlow
import io.kotest.assertions.failure
import io.kotest.core.spec.IsolationMode
import io.kotest.koin.KoinListener
import io.kotest.matchers.shouldBe
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


    private suspend fun createCompositionAndGetId(): Int {
        val authorId = signupFlow.signupReturnId(AuthUtilities.createAuthorRequest)
        val layoutId = spaceRepository.insertNewLayout(name = "That was lagitness")
        val res = carouselOfImagesManager.createComposition(createCarouselBasicImagesReq, layoutId, authorId)
        res.isSuccess shouldBe true
        res.data ?: throw failure("failed to return composition id at setup")
        return res.data!!
    }

    init {
        beforeEach { clearAllMocks() }

        given("getSingleCompositionOfPrivilegedAuthor") {
            then("success") {
                rollback {
                    val compositionId = createCompositionAndGetId()
//                    val composition = carouselOfImagesRepository.getSingleCompositionOfPrivilegedAuthor(compositionId)
//                    println(composition)
                }
            }
        }

//        given("getSingleCompositionOfPrivilegedAuthor") {
//            then("success") {
//                rollback { }
//            }
//        }

        given("compose") {
            then("success") {
                rollback { }
            }
        }

    }
}
