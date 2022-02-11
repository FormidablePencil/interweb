package integrationTests.compositions.flows

import com.idealIntent.dtos.compositions.NewUserComposition
import com.idealIntent.exceptions.logInfo
import com.idealIntent.managers.compositions.carousels.CarouselOfImagesManager
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.services.CompositionService
import dtos.compositions.CompositionCategory
import dtos.compositions.carousels.CompositionCarouselType
import integrationTests.auth.flows.AuthUtilities
import integrationTests.auth.flows.SignupFlow
import io.kotest.assertions.failure
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.koin.core.component.inject
import shared.testUtils.BehaviorSpecFlow
import shared.testUtils.carouselPrivateBasicImagesReqSerialized
import shared.testUtils.carouselPublicBasicImagesReqSerialized
import shared.testUtils.createPrivateCarouselBasicImagesReq

class CompositionFlow : BehaviorSpecFlow() {
    companion object {
        val userComposition = NewUserComposition(
            compositionCategory = CompositionCategory.Carousel,
            compositionType = CompositionCarouselType.BasicImages.value,
        )
        val layoutName = "That was legitness"
    }

    private val compositionService: CompositionService by inject()
    private val signupFlow: SignupFlow by inject()
    private val spaceRepository: SpaceRepository by inject()
    private val carouselOfImagesManager: CarouselOfImagesManager by inject()

    suspend fun signup_then_createComposition(publicView: Boolean): Triple<Int, Int, Int> {
        val authorId = signupFlow.signupReturnId(AuthUtilities.createAuthorRequest)
        val layoutId = spaceRepository.insertNewLayout(name = layoutName, authorId = authorId)
        val compositionSourceId = createComposition(publicView, layoutId, authorId)
        return Triple(compositionSourceId, layoutId, authorId)
    }

    private fun createComposition(public: Boolean, layoutId: Int, authorId: Int): Int {
        val res = compositionService.createComposition(
            userComposition = userComposition,
            jsonData = if (public) carouselPublicBasicImagesReqSerialized else carouselPrivateBasicImagesReqSerialized,
            layoutId, authorId
        )
        res.isSuccess shouldBe true
        res.data ?: throw failure("failed to return composition id at setup")

        // validate that composition was created
        val resGetComp = if (public) carouselOfImagesManager.getPublicComposition(res.data!!)
        else carouselOfImagesManager.getPrivateComposition(res.data!!, authorId)

        resGetComp ?: throw failure("failed to get created composition")

        resGetComp.images.size shouldBe createPrivateCarouselBasicImagesReq.images.size
        resGetComp.imgOnclickRedirects.size shouldBe createPrivateCarouselBasicImagesReq.imgOnclickRedirects.size
        resGetComp.name shouldBe createPrivateCarouselBasicImagesReq.name

        resGetComp.images.forEach {
            createPrivateCarouselBasicImagesReq.images.find { item ->
                it.orderRank == item.orderRank
                        && it.url == item.url
                        && it.description == item.description
            } shouldNotBe null
        }
        resGetComp.imgOnclickRedirects.forEach {
            createPrivateCarouselBasicImagesReq.imgOnclickRedirects.find { item ->
                item.orderRank == it.orderRank
                        && item.text == it.text
            } shouldNotBe null
        }

        logInfo("Created composition: ${resGetComp.toString()}", this::class.java)

        return res.data!!
    }
}