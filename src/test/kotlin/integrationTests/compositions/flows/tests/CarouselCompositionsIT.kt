package integrationTests.compositions.flows.tests

import com.google.gson.Gson
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.compositions.NewUserComposition
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.compositions.carousels.CreateCarouselBasicImagesReq
import com.idealIntent.managers.SpaceManager
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import com.idealIntent.services.CompositionService
import dtos.compositions.CompositionCategory
import dtos.compositions.carousels.CompositionCarouselType
import integrationTests.auth.flows.AuthUtilities
import integrationTests.auth.flows.SignupFlow
import integrationTests.compositions.flows.CompositionFlow
import io.kotest.assertions.failure
import org.koin.test.inject
import shared.testUtils.BehaviorSpecIT
import shared.testUtils.images
import shared.testUtils.rollback
import shared.testUtils.texts

class CarouselCompositionsIT : BehaviorSpecIT({
    val signupFlow: SignupFlow by inject()
    val compositionService: CompositionService by inject()
    val compositionFlow: CompositionFlow by inject()
    val spaceManager: SpaceManager by inject()
    val gson = Gson()

    given("Carousel of images") {
        val createCarouselBasicImagesReq = CreateCarouselBasicImagesReq(
            "Projects", images, texts, listOf<PrivilegedAuthor>(), privilegeLevel = 1
        )
        val createCarouselBasicImagesReqSerialized =
            gson.toJson(createCarouselBasicImagesReq, CreateCarouselBasicImagesReq::class.java)

        suspend fun setupCreateComposition(): Triple<CompositionResponse, Int, Int> {
            val authorId = signupFlow.signupReturnId(AuthUtilities.createAuthorRequest)
            val layoutId = compositionService.createNewLayout(
                name = CompositionFlow.layoutName, authorId = authorId
            ).data ?: throw failure("Failed to get id of newly created layout.")

            val compositionSourceId = compositionService.createComposition(
                userComposition = NewUserComposition(
                    compositionCategory = CompositionCategory.Carousel,
                    compositionType = CompositionCarouselType.BasicImages.value
                ),
                compositionSerialized = createCarouselBasicImagesReqSerialized,
                layoutId = layoutId,
                userId = authorId,
            )
            return Triple(compositionSourceId, layoutId, authorId)
        }

        and("create publicly viewable composition") {
            then("Get single composition") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) = setupCreateComposition()
//                    TODO("Test can't be done until CompositionDataBuilder from getPrivateLayoutOfCompositions is implemented")

                    // region temp
                    compositionService.createComposition(
                        userComposition = NewUserComposition(
                            compositionCategory = CompositionCategory.Carousel,
                            compositionType = CompositionCarouselType.BasicImages.value
                        ),
                        compositionSerialized = createCarouselBasicImagesReqSerialized,
                        layoutId = layoutId,
                        userId = authorId,
                    )

                    compositionService.createComposition(
                        userComposition = NewUserComposition(
                            compositionCategory = CompositionCategory.Carousel,
                            compositionType = CompositionCarouselType.BasicImages.value
                        ),
                        compositionSerialized = createCarouselBasicImagesReqSerialized,
                        layoutId = layoutId,
                        userId = authorId,
                    )

                    val res = spaceManager.getPrivateLayoutOfCompositions(layoutId = layoutId, authorId = authorId)
                    res.getCompositionsOfLayouts()
                    println(res)


                    val sourceId = res.carouselOfImagesData.get()[0].sourceId

//                    val carouselOfImagesRepository: CarouselOfImagesRepository by inject()
//                    val result = carouselOfImagesRepository.getPrivateComposition(sourceId, authorId)
//                    println(result)
                    // endregion


//                    val res = compositionService.getPrivateLayoutOfCompositions(layoutId)


//                    res ?: throw failure("failed to get created composition")
//
//                    res.images.size shouldBe createPrivateCarouselBasicImagesReq.images.size
//                    res.imgOnclickRedirects.size shouldBe createPrivateCarouselBasicImagesReq.imgOnclickRedirects.size
//                    res.name shouldBe createPrivateCarouselBasicImagesReq.name
//
//                    res.images.forEach {
//                        createPrivateCarouselBasicImagesReq.images.find { item ->
//                            it.orderRank == item.orderRank
//                                    && it.url == item.url
//                                    && it.description == item.description
//                        } shouldNotBe null
//                    }
//                    res.imgOnclickRedirects.forEach {
//                        createPrivateCarouselBasicImagesReq.imgOnclickRedirects.find { item ->
//                            item.orderRank == it.orderRank
//                                    && item.text == it.text
//                        } shouldNotBe null
//                    }
//
//                    logInfo("Created composition: ${res.toString()}", this::class.java)
//
//                    return res.data!!
                }
            }

            then("Update composition") {
                rollback {

                }
            }

            then("Delete composition") {
                rollback {

                }
            }
        }
    }
})