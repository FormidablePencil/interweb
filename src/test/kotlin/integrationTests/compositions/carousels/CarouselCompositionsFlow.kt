package integrationTests.compositions.carousels

import com.idealIntent.dtos.compositions.NewUserComposition
import com.idealIntent.dtos.compositions.carousels.*
import com.idealIntent.exceptions.logInfo
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.managers.compositions.carousels.CarouselOfImagesManager
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.services.CompositionService
import dtos.compositions.CompositionCategory
import dtos.compositions.carousels.CompositionCarouselType
import integrationTests.auth.flows.SignupFlow
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
    private val spaceRepository: SpaceRepository by inject()
    private val compositionPrivilegesManager: CompositionPrivilegesManager by inject()
    private val imageRepository: ImageRepository by inject()
    private val textRepository: TextRepository by inject()
    private val signupFlow: SignupFlow by inject()
    private val userComposition = NewUserComposition(
        compositionCategory = CompositionCategory.Carousel,
        compositionType = CompositionCarouselType.BasicImages.value,
    )

    companion object {
        const val layoutName = "layout with carousel comp"
        val carouselBasicImagesCreateReq = CarouselBasicImagesCreateReq(
            "Projects", images, texts, listOf(), privilegeLevel = 0
        )
//        val carouselBlurredOverlayCreateReq = CarouselBlurredOverlayCreateReq(
//
//        )

        fun validateDataResponse(res: CarouselBasicImagesRes, isPublic: Boolean = true) {
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

        fun validateDataResponse(res: CarouselBlurredOverlayCreateReq, isPublic: Boolean = true) {
            TODO()
        }
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

    suspend fun prepareComposition(res: CarouselBasicImagesCreateReq): Pair<CarouselOfImagesComposePrepared, Int> {
        val authorId = signupFlow.signupReturnId()
        val createRequest = CarouselBasicImagesCreateReq(
            name = "that was legitness",
            images = listOf(),
            imgOnclickRedirects = listOf(),
            privilegedAuthors = listOf(),
            privilegeLevel = 0,
        )
        val layoutId = spaceRepository.insertNewLayout(createRequest.name, authorId)

        val imageCollectionId =
            imageRepository.batchInsertRecordsToNewCollection(createRequest.images)
        val redirectsCollectionId = textRepository.batchInsertRecordsToNewCollection(
            createRequest.imgOnclickRedirects
        )

        val compositionSourceId = compositionPrivilegesManager.createCompositionSource(
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

        return Pair(
            CarouselOfImagesComposePrepared(
                name = createRequest.name,
                imageCollectionId = imageCollectionId,
                redirectTextCollectionId = redirectsCollectionId,
                sourceId = compositionSourceId,
            ),
            compositionSourceId
        )
    }

    suspend fun prepareComposition(res: CarouselBlurredOverlayRes): Pair<CarouselBlurredOverlayComposePrepared, Int> {
        TODO()
        val authorId = signupFlow.signupReturnId()
        val createRequest = CarouselBasicImagesCreateReq(
            name = "that was legitness",
            images = listOf(),
            imgOnclickRedirects = listOf(),
            privilegedAuthors = listOf(),
            privilegeLevel = 0,
        )
        val layoutId = spaceRepository.insertNewLayout(createRequest.name, authorId)

        val imageCollectionId =
            imageRepository.batchInsertRecordsToNewCollection(createRequest.images)
        val redirectsCollectionId = textRepository.batchInsertRecordsToNewCollection(
            createRequest.imgOnclickRedirects
        )

        val compositionSourceId = compositionPrivilegesManager.createCompositionSource(
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
//
//        return Pair(
//            CarouselBlurredOverlayComposePrepared(
//                name = createRequest.name,
//                imageCollectionId = imageCollectionId,
//                redirectTextCollectionId = redirectsCollectionId,
//                sourceId = compositionSourceId,
//            ),
//            compositionSourceId
//        )
    }
}