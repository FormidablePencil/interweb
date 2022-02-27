package integrationTests.compositions.grids

import com.google.gson.Gson
import com.idealIntent.dtos.collectionsGeneric.images.Image
import com.idealIntent.dtos.collectionsGeneric.texts.Text
import com.idealIntent.dtos.compositions.NewUserComposition
import com.idealIntent.dtos.compositions.grids.GridOneOffCreateReq
import com.idealIntent.dtos.compositions.grids.GridOneOffRes
import com.idealIntent.exceptions.logInfo
import com.idealIntent.managers.compositions.grids.GridOneOffManager
import com.idealIntent.services.CompositionService
import dtos.compositions.CompositionCategory
import dtos.compositions.grids.CompositionGrid
import io.kotest.assertions.failure
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.koin.core.component.inject
import shared.testUtils.BehaviorSpecFlow

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
            collectionOf_titles_of_image_categories = listOf(
                Text(10000, "Backend"),
                Text(20000, "Frontend"),
                Text(30000, "Web3")
            ),
            collectionOf_images_2d = listOf(
                Pair(
                    10000,
                    listOf(
                        Image(
                            orderRank = 10000,
                            description = "Kotlin",
                            url = "https://i.ibb.co/ZT1xnVL/kotlin.png"
                        ),
                        Image(
                            orderRank = 20000,
                            description = "C#",
                            url = "https://i.ibb.co/tZzVyrx/c-Sharp-Icon.png"
                        ),
                        Image(
                            orderRank = 30000,
                            description = "Node.js",
                            url = "https://i.ibb.co/Pm9X8Jq/Node.png"
                        )
                    ),
                ),
                Pair(
                    20000,
                    listOf(
                        Image(
                            orderRank = 10000,
                            description = "React",
                            url = "https://i.ibb.co/nb965ST/react-Logo.png"
                        ),
                        Image(
                            orderRank = 20000,
                            description = "Vue",
                            url = "https://i.ibb.co/5Kn9mXG/vue.png"
                        ),
                        Image(
                            orderRank = 30000,
                            description = "Angular",
                            url = "https://i.ibb.co/Gv6tzn0/angular.png"
                        ),
                        Image(
                            orderRank = 40000,
                            description = "Swift (IOS)",
                            url = "https://i.ibb.co/nb965ST/react-Logo.png"
                        ),
                        Image(
                            orderRank = 50000,
                            description = "Kotlin (Android)",
                            url = "https://i.ibb.co/nb965ST/react-Logo.png"
                        ),
                    )
                ),
                Pair(
                    20000,
                    listOf(
                        Image(
                            orderRank = 10000,
                            description = "Solidity",
                            url = "https://i.ibb.co/GpbjmWH/Solidity-Logo-wine.png"
                        ),
                        Image(
                            orderRank = 20000,
                            description = "Vyper",
                            url = "https://i.ibb.co/86zBWc5/Vyper-1324888772874241257.png"
                        ),
                        Image(
                            orderRank = 30000,
                            description = "Arbitrum",
                            url = "https://i.ibb.co/D9gKh2Z/Arbitrum-Symbol-Full-color-White-background.png"
                        ),
                        Image(
                            orderRank = 40000,
                            description = "Unit testing",
                            url = "https://i.ibb.co/nb965ST/react-Logo.png"
                        ),
                        Image(
                            orderRank = 50000,
                            description = "Integration testing",
                            url = "https://i.ibb.co/nb965ST/react-Logo.png"
                        ),
                        Image(
                            orderRank = 60000,
                            description = "End to end testing",
                            url = "https://i.ibb.co/nb965ST/react-Logo.png"
                        ),
                    )
                )
            ),
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

    fun validateGridOneOff(response: GridOneOffRes, isPublic: Boolean) {
        with(privateGridOneOffReq) {
            response.privilegeLevel shouldBe if (isPublic) 1 else 0
            response.collectionOf_titles_of_image_categories.size shouldBe collectionOf_titles_of_image_categories.size
            response.collectionOf_images_2d.size shouldBe collectionOf_images_2d.size
            response.collectionOf_img_descriptions.size shouldBe collectionOf_img_descriptions.size
            response.collectionOf_onclick_redirects.size shouldBe collectionOf_onclick_redirects.size
            response.name shouldBe name
            response.privilegedAuthors shouldBe privilegedAuthors


            collectionOf_titles_of_image_categories.forEach {
                response.collectionOf_titles_of_image_categories.find { item ->
                    it.orderRank == item.orderRank
                            && it.text == item.text
                } shouldNotBe null
            }

            collectionOf_images_2d.forEach {
                val found = response.collectionOf_images_2d.find { item ->
                    it.first == item.first
                } ?: throw failure("did not find item by order rank")

                it.second.forEach {
                    found.second.find { item ->
                        item.orderRank == it.orderRank
                                && item.url == it.url
                                && item.description == it.description
                    } shouldNotBe null
                }
            }

            collectionOf_img_descriptions.forEach {
                val found = response.collectionOf_img_descriptions.find { item ->
                    it.first == item.first
                } ?: throw failure("did not find item by order rank")

                it.second.forEach {
                    found.second.find { item ->
                        item.orderRank == it.orderRank
                                && item.text == it.text
                    } shouldNotBe null
                }
            }

            collectionOf_onclick_redirects.forEach {
                val found = response.collectionOf_onclick_redirects.find { item ->
                    it.first == item.first
                } ?: throw failure("did not find item by order rank")

                it.second.forEach {
                    found.second.find { item ->
                        item.orderRank == it.orderRank
                                && item.text == it.text
                    } shouldNotBe null
                }
            }

            privilegedAuthors.forEach {
                response.privilegedAuthors.find { item ->
                    it.username == item.username
                            && it.modify == item.modify
                            && it.deletion == item.deletion
                            && it.modifyUserPrivileges == item.modifyUserPrivileges
                } shouldNotBe null
            }
        }
    }

    fun createComposition(isPublic: Boolean, layoutId: Int, authorId: Int): Int {
        val res = compositionService.createComposition(
            userComposition = userComposition,
            compositionSerialized = if (isPublic) publicGridOneOffReqSerialized else privateGridOneOffReqSerialized,
            layoutId, authorId
        )
        res.isSuccess shouldBe true
        res.data ?: throw failure("failed to return composition id at setup")

        // validate that composition was created
        val resGetComp = if (isPublic) gridOneOffManager.getPublicComposition(res.data!!)
        else gridOneOffManager.getPrivateComposition(res.data!!, authorId)

        resGetComp ?: throw failure("failed to get created composition")

        validateGridOneOff(resGetComp, isPublic)

        logInfo("Created composition: ${resGetComp.toString()}", this::class.java)

        return res.data!!
    }
}