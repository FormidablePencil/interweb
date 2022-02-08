package integrationTests.space.tests

import com.google.gson.Gson
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.compositions.carousels.CreateCarouselBasicImagesReq
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import com.idealIntent.services.CompositionService
import dtos.compositions.CompositionCategory
import dtos.compositions.carousels.CompositionCarousel
import integrationTests.auth.flows.SignupFlow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.koin.test.inject
import shared.testUtils.BehaviorSpecIT
import shared.testUtils.images
import shared.testUtils.rollback
import shared.testUtils.texts

class CompositionIT : BehaviorSpecIT({
    val signupFlow: SignupFlow by inject()
    val cmsService: CompositionService by inject()
    val carouselOfImagesRepository: CarouselOfImagesRepository by inject()
    val gson = Gson()

    val createCarouselBasicImagesReq = CreateCarouselBasicImagesReq("Projects", images, texts, listOf())

    given("create a private layout and then create a few compositions for the layout") {
        then("get private layout of compositions") {
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

                res.isSuccess shouldBe true
                res.data shouldNotBe null

                val compositionBuilder = cmsService.getPrivateLayoutOfCompositions(layoutId, userId)
                compositionBuilder.getCompositionsOfLayouts()
            }
        }
    }
})