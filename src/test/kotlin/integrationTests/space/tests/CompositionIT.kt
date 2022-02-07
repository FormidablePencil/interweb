package integrationTests.space.tests

import com.google.gson.Gson
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.compositions.carousels.CreateCarouselBasicImagesReq
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import com.idealIntent.services.CmsService
import dtos.compositions.CompositionCategory
import dtos.compositions.carousels.CompositionCarousel
import integrationTests.auth.flows.SignupFlow
import io.kotest.assertions.failure
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.koin.test.inject
import shared.testUtils.BehaviorSpecIT
import shared.testUtils.images
import shared.testUtils.rollback
import shared.testUtils.texts

class CompositionIT : BehaviorSpecIT({
    val signupFlow: SignupFlow by inject()
    val cmsService: CmsService by inject()
    val carouselOfImagesRepository: CarouselOfImagesRepository by inject()
    val gson = Gson()

    val createCarouselBasicImagesReq = CreateCarouselBasicImagesReq("Projects", images, texts, listOf())

    given("createComposition") {
        And("Carousel") {
            And("BasicImages") {
                then("success") {
                    rollback {
                        // region setup
                        val userId = signupFlow.signupReturnId()
                        // endregion

                        val layoutId = cmsService.createNewLayout("my new layout")

                        val res: CompositionResponse = cmsService.createComposition(
                            CompositionCategory.Carousel,
                            CompositionCarousel.BasicImages,
                            gson.toJson(createCarouselBasicImagesReq),
                            layoutId,
                            userId
                        )

                        // region validate
                        res.isSuccess shouldBe true
                        res.data shouldNotBe null
                        val compositionId = res.data

                        val compRes =
                            carouselOfImagesRepository.getSingleCompositionOfPrivilegedAuthor(compositionId!!, userId)
                                ?: throw failure("didn't return composition by id")

//                        compRes.images.forEach {
//
//                        }
                        // region
                    }
                }
            }
        }
    }
})