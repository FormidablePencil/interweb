package integrationTests.compositions.carousels

import com.google.gson.Gson
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.compositions.NewUserComposition
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.dtos.compositions.carousels.CreateCarouselBasicImagesReq
import com.idealIntent.managers.SpaceManager
import com.idealIntent.services.CompositionService
import dtos.compositions.CompositionCategory
import dtos.compositions.carousels.CompositionCarouselType
import dtos.compositions.texts.BasicText
import integrationTests.auth.flows.AuthUtilities
import integrationTests.auth.flows.SignupFlow
import io.kotest.assertions.failure
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.koin.test.inject
import shared.testUtils.*

// todo - left to do:
//  Library of compositions
//  SpaceRepository and SpaceManager test
//  Unit test, document and harden codebase

class CarouselCompositionIT : BehaviorSpecIT({
    val signupFlow: SignupFlow by inject()
    val compositionService: CompositionService by inject()
    val carouselCompositionFlow: CarouselCompositionFlow by inject()
    val spaceManager: SpaceManager by inject()
    val gson = Gson()

    given("Carousel of images") {

        val createBasicText = BasicText(
            name = "My favorite basic text.",
            text = "Hello, my neighbors!"
        )
        val createBasicTextSerialized = gson.toJson(createBasicText)


        suspend fun setupCreateComposition(): Triple<Int, Int, Int> {
            val authorId = signupFlow.signupReturnId(AuthUtilities.createAuthorRequest)
            val layoutId = compositionService.createNewLayout(
                name = carouselCompositionFlow.layoutName, authorId = authorId
            ).data ?: throw failure("Failed to get id of newly created layout.")

            // todo there will be many components so we can't rely on returning ids of compositions
            val compositionSourceId = carouselCompositionFlow.createComposition(public = false, layoutId, authorId)

            return Triple(compositionSourceId, layoutId, authorId)
        }

        and("create publicly viewable of all compositions under a layout") {

            then("get layout of compositions") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) = setupCreateComposition()
//                    TODO("Test can't be done until CompositionDataBuilder from getPrivateLayoutOfCompositions is implemented")

                    val res = spaceManager.getPrivateLayoutOfCompositions(layoutId = layoutId, authorId = authorId)
                    val wrappedPresent = res.getCompositionsOfLayouts()
                    println(wrappedPresent)

                    val unwrapPresent =
                        gson.fromJson(wrappedPresent[0].serializedComposition, CreateCarouselBasicImagesReq::class.java)
                    unwrapPresent.images.size shouldBe createPrivateCarouselBasicImagesReq.images.size
                    unwrapPresent.imgOnclickRedirects.size shouldBe createPrivateCarouselBasicImagesReq.imgOnclickRedirects.size
                    unwrapPresent.name shouldBe createPrivateCarouselBasicImagesReq.name

                    unwrapPresent.images.forEach {
                        createPrivateCarouselBasicImagesReq.images.find { item ->
                            it.orderRank == item.orderRank
                                    && it.url == item.url
                                    && it.description == item.description
                        } shouldNotBe null
                    }
                    unwrapPresent.imgOnclickRedirects.forEach {
                        createPrivateCarouselBasicImagesReq.imgOnclickRedirects.find { item ->
                            item.orderRank == it.orderRank
                                    && item.text == it.text
                        } shouldNotBe null
                    }
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