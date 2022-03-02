package integrationTests.compositions.carousels

import com.idealIntent.dtos.compositions.NewUserComposition
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesCreateReq
import com.idealIntent.exceptions.logInfo
import com.idealIntent.managers.compositions.carousels.CarouselOfImagesManager
import com.idealIntent.services.CompositionService
import dtos.compositions.CompositionCategory
import dtos.compositions.carousels.CompositionCarouselType
import io.kotest.assertions.failure
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.koin.core.component.inject
import shared.testUtils.*

/**
 * Carousel composition flow. Used for code shortcuts.
 *
 * Instead of needing to repeat code this class was created to hold code that is or like to be used in multiple places.
 */
class CarouselCompositionsFlow : BehaviorSpecFlow() {
    private val compositionService: CompositionService by inject()
    private val carouselOfImagesManager: CarouselOfImagesManager by inject()
    private val userComposition = NewUserComposition(
        compositionCategory = CompositionCategory.Carousel,
        compositionType = CompositionCarouselType.BasicImages.value,
    )

    companion object {
        const val layoutName = "layout with carousel comp"
        val carouselBasicImagesCreateReq = CarouselBasicImagesCreateReq(
            "Projects", images, texts, listOf(), privilegeLevel = 0
        )
    }

    fun createComposition(public: Boolean, layoutId: Int, authorId: Int): Int {
        val res = compositionService.createComposition(
            userComposition = userComposition,
            compositionSerialized = if (public) carouselPublicBasicImagesReqSerialized else carouselPrivateBasicImagesReqSerialized,
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