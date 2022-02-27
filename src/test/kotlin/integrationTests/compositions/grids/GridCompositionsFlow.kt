package integrationTests.compositions.grids

import com.google.gson.Gson
import com.idealIntent.dtos.compositions.NewUserComposition
import com.idealIntent.dtos.compositions.grids.GridOneOffCreateReq
import com.idealIntent.exceptions.logInfo
import com.idealIntent.managers.compositions.grids.GridOneOffManager
import com.idealIntent.services.CompositionService
import dtos.compositions.CompositionCategory
import dtos.compositions.grids.CompositionGrid
import io.kotest.assertions.failure
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.koin.core.component.inject
import shared.testUtils.*

class GridCompositionsFlow : BehaviorSpecFlow() {
    private val compositionService: CompositionService by inject()
    private val gridOneOffManager: GridOneOffManager by inject()
    private val userComposition = NewUserComposition(
        compositionCategory = CompositionCategory.Grid,
        compositionType = CompositionGrid.Basic.value, // todo rename to OneOff
    )
    val layoutName = "layout with grid comp"

    companion object {
        private val gson = Gson()
        val layoutName = "That was legitness"
        val publicGridOneOffReq = GridOneOffCreateReq(
            // todo add lists of items
            collectionOf_titles_of_image_categories = listOf(),
            collectionOf_images_2d = listOf(),
            collectionOf_img_descriptions = listOf(),
            collectionOf_onclick_redirects = listOf(),
            privilegeLevel = 0,
            name = "name",
            privilegedAuthors = listOf(),
        )
        val privateGridOneOffReq = publicGridOneOffReq.let {
            GridOneOffCreateReq(
                collectionOf_titles_of_image_categories = it.collectionOf_titles_of_image_categories,
                collectionOf_images_2d = it.collectionOf_images_2d,
                collectionOf_img_descriptions = it.collectionOf_img_descriptions,
                collectionOf_onclick_redirects = it.collectionOf_onclick_redirects,
                privilegeLevel = 1,
                name = it.name,
                privilegedAuthors = it.privilegedAuthors,
            )
        }
        val publicGridOneOffReqSerialized = gson.toJson(publicGridOneOffReq)
        val privateGridOneOffReqSerialized = gson.toJson(privateGridOneOffReq)
    }

    fun createComposition(public: Boolean, layoutId: Int, authorId: Int): Int {
        val res = compositionService.createComposition(
            userComposition = userComposition,
            compositionSerialized = if (public) publicGridOneOffReqSerialized else privateGridOneOffReqSerialized,
            layoutId, authorId
        )
        res.isSuccess shouldBe true
        res.data ?: throw failure("failed to return composition id at setup")

        // validate that composition was created
        val resGetComp = if (public) gridOneOffManager.getPublicComposition(res.data!!)
        else gridOneOffManager.getPrivateComposition(res.data!!, authorId)

        resGetComp ?: throw failure("failed to get created composition")

        with(privateGridOneOffReq) {
            resGetComp.collectionOf_titles_of_image_categories.size shouldBe collectionOf_titles_of_image_categories.size
            resGetComp.collectionOf_images_2d.size shouldBe collectionOf_images_2d.size
            resGetComp.collectionOf_img_descriptions.size shouldBe collectionOf_img_descriptions.size
            resGetComp.collectionOf_onclick_redirects.size shouldBe collectionOf_onclick_redirects.size
            resGetComp.name shouldBe name
            resGetComp.privilegedAuthors shouldBe privilegedAuthors

            resGetComp.collectionOf_titles_of_image_categories.forEach {
                collectionOf_titles_of_image_categories.find { item ->
                    it.orderRank == item.orderRank
                            && it.text == item.text
                } shouldNotBe null
            }

            resGetComp.collectionOf_titles_of_image_categories.forEach {
                collectionOf_titles_of_image_categories.find { item ->
                    it.orderRank == item.orderRank
                            && it.text == item.text
                } shouldNotBe null
            }

//            resGetComp.imgOnclickRedirects.forEach {
//                createPrivateCarouselBasicImagesReq.imgOnclickRedirects.find { item ->
//                    item.orderRank == it.orderRank
//                            && item.text == it.text
//                } shouldNotBe null
//            }
        }

        logInfo("Created composition: ${resGetComp.toString()}", this::class.java)

        return res.data!!
    }
}