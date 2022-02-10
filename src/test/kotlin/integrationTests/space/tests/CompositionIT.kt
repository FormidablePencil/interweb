package integrationTests.space.tests

import com.google.gson.Gson
import com.idealIntent.dtos.compositions.ExistingUserComposition
import com.idealIntent.dtos.compositions.NewUserComposition
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.compositions.carousels.CreateCarouselBasicImagesReq
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import com.idealIntent.services.CompositionService
import dtos.compositions.CompositionCategory
import dtos.compositions.carousels.CompositionCarouselType
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
    val compositionService: CompositionService by inject()
    val carouselOfImagesRepository: CarouselOfImagesRepository by inject()
    val gson = Gson()

    val createCarouselBasicImagesReq = CreateCarouselBasicImagesReq(
        "Projects", images, texts, listOf(), privilegeLevel = 0
    )

    // todo - the more extensive tests will be done with repository tests
    given("create a private layout and then create a few compositions for the layout") {
        then("get private layout of compositions") {
            rollback {
                // region setup
                val userId = signupFlow.signupReturnId()
                // endregion

                val layoutId = compositionService.createNewLayout("my new layout", userId)

                val res: CompositionResponse = compositionService.createComposition(
                    NewUserComposition(CompositionCategory.Carousel, CompositionCarouselType.BasicImages.value),
                    gson.toJson(createCarouselBasicImagesReq),
                    layoutId,
                    userId
                )

                res.isSuccess shouldBe true
                res.data shouldNotBe null

                val compositionBuilder = compositionService.getPrivateLayoutOfCompositions(layoutId, userId)
                compositionBuilder.getCompositionsOfLayouts()
            }
        }

        then("delete composition") {
            rollback {
                // create account, layout and create compositions under layout
                val userId = signupFlow.signupReturnId()
                val layoutId = compositionService.createNewLayout("my new layout", userId)

                val res: CompositionResponse = compositionService.createComposition(
                    NewUserComposition(CompositionCategory.Carousel, CompositionCarouselType.BasicImages.value),
                    gson.toJson(createCarouselBasicImagesReq),
                    layoutId,
                    userId
                )

                res.data shouldNotBe null
                val carouselOfImagesCompositionId = res.data
                    ?: throw failure("Failed to get is of composition source")

                // validate that the compositions where created
                val compositionBuilder = compositionService.getPrivateLayoutOfCompositions(layoutId, userId)

                // delete composition
                val deleteRes = compositionService.deleteComposition(
                    userComposition = ExistingUserComposition(
                        compositionSourceId = carouselOfImagesCompositionId,
                        compositionCategory = CompositionCategory.Carousel,
                        compositionType = CompositionCarouselType.BasicImages.value,
                    ),
                    authorId = userId
                )

                // validate that the composition was deleted
                val compositionBuilderAfterDeletion =
                    compositionService.getPrivateLayoutOfCompositions(layoutId, userId)
            }
        }

        then("update composition") {
            rollback {}
        }
    }
})