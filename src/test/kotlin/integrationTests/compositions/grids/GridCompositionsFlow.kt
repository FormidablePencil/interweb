package integrationTests.compositions.grids

import com.google.gson.Gson
import com.idealIntent.dtos.collectionsGeneric.images.Image
import com.idealIntent.dtos.collectionsGeneric.images.ImagePK
import com.idealIntent.dtos.collectionsGeneric.texts.Text
import com.idealIntent.dtos.collectionsGeneric.texts.TextPK
import com.idealIntent.dtos.compositions.NewUserComposition
import com.idealIntent.dtos.compositions.grids.GridItem
import com.idealIntent.dtos.compositions.grids.GridOneOffComposePrepared
import com.idealIntent.dtos.compositions.grids.GridOneOffCreateReq
import com.idealIntent.dtos.compositions.grids.GridOneOffRes3
import com.idealIntent.exceptions.logInfo
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.managers.compositions.grids.GridOneOffManager
import com.idealIntent.managers.compositions.images.D2ImageRepository
import com.idealIntent.managers.compositions.texts.D2TextRepository
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.services.CompositionService
import dtos.compositions.CompositionCategory
import dtos.compositions.grids.CompositionGrid
import integrationTests.auth.flows.SignupFlow
import io.kotest.assertions.failure
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.koin.core.component.inject
import shared.testUtils.BehaviorSpecFlow

class GridCompositionsFlow : BehaviorSpecFlow() {
    private val compositionService: CompositionService by inject()
    private val gridOneOffManager: GridOneOffManager by inject()
    private val imageRepository: ImageRepository by inject()
    private val textRepository: TextRepository by inject()
    private val d2ImageRepository: D2ImageRepository by inject()
    private val d2TextRepository: D2TextRepository by inject()
    private val spaceRepository: SpaceRepository by inject()
    private val compositionPrivilegesManager: CompositionPrivilegesManager by inject()
    private val signupFlow: SignupFlow by inject()
    private val userComposition = NewUserComposition(
        compositionCategory = CompositionCategory.Grid,
        compositionType = CompositionGrid.Basic.value, // todo rename to OneOff
    )
    val layoutName = "layout with grid comp"

    companion object {
        private val gson = Gson()
        const val layoutName = "That was legitness"
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
        val publicGridOneOffReqSerialized: String = gson.toJson(publicGridOneOffReq)
        val privateGridOneOffReqSerialized: String = gson.toJson(privateGridOneOffReq)

        fun convertToGrid3(): GridOneOffRes3 {
            val gridItems = mutableListOf<GridItem>()

            privateGridOneOffReq.collectionOf_titles_of_image_categories.forEach { item ->
                // collection of images
                val colOf_images_2d = privateGridOneOffReq.collectionOf_images_2d.find { it.first == item.orderRank }
                    ?: throw Exception("")

                val images_2d = colOf_images_2d.second.map {
                    return@map ImagePK(
                        id = 0,
                        orderRank = it.orderRank,
                        url = it.url,
                        description = it.description,
                    )
                }

                // collection of image descriptions
                val colOf_img_descriptions = privateGridOneOffReq.collectionOf_img_descriptions.find {
                    it.first == item.orderRank
                } ?: throw Exception("")

                val img_descriptions = colOf_img_descriptions.second.map {
                    return@map TextPK(
                        id = 0,
                        orderRank = it.orderRank,
                        text = it.text,
                    )
                }

                // collection of onclick redirection links
                val colOf_onclick_redirects = privateGridOneOffReq.collectionOf_onclick_redirects.find {
                    it.first == item.orderRank
                } ?: throw Exception("")

                val onclick_redirects = colOf_onclick_redirects.second.map {
                    return@map TextPK(
                        id = 0,
                        orderRank = it.orderRank,
                        text = it.text,
                    )
                }

                gridItems += GridItem(
                    title = item.text,
                    images_2d = images_2d.toMutableList(),
                    img_descriptions = img_descriptions.toMutableList(),
                    onclick_redirects = onclick_redirects.toMutableList(),
                )
            }

            return GridOneOffRes3(
                id = 0,
                sourceId = 0,
                gridItems = gridItems,
                privilegeLevel = privateGridOneOffReq.privilegeLevel,
                name = privateGridOneOffReq.name,
                privilegedAuthors = privateGridOneOffReq.privilegedAuthors,
            )
        }

        fun validateDataResponse(response: GridOneOffRes3, isPublic: Boolean = true) {
            val gridItemOriginal = convertToGrid3()
            response.gridItems.size shouldBe gridItemOriginal.gridItems.size
            response.name shouldBe gridItemOriginal.name
            response.privilegeLevel shouldBe if (isPublic) 1 else 0
            response.privilegedAuthors shouldBe gridItemOriginal.privilegedAuthors

            response.privilegedAuthors.forEach { item ->
                gridItemOriginal.privilegedAuthors.find {
                    it.username == item.username
                            && it.modify == item.modify
                            && it.deletion == item.deletion
                            && it.modifyUserPrivileges == item.modifyUserPrivileges
                } shouldNotBe null
            }

            gridItemOriginal.gridItems.forEach { originalItem ->
                response.gridItems.forEach { resItem ->

                    originalItem.title shouldBe resItem.title
                    originalItem.images_2d.size shouldBe resItem.images_2d.size
                    originalItem.img_descriptions.size shouldBe resItem.img_descriptions.size
                    originalItem.onclick_redirects.size shouldBe resItem.onclick_redirects.size

                    originalItem.images_2d.forEach { orig ->
                        resItem.images_2d.find {
                            orig.url == it.url && orig.description == it.description && orig.orderRank == it.orderRank
                        } ?: throw failure("items not the same")
                    }

                    originalItem.img_descriptions.forEach { orig ->
                        resItem.img_descriptions.find {
                            orig.text == it.text && orig.orderRank == it.orderRank
                        } ?: throw failure("items not the same")
                    }

                    originalItem.onclick_redirects.forEach { orig ->
                        resItem.onclick_redirects.find {
                            orig.text == it.text && orig.orderRank == it.orderRank
                        } ?: throw failure("items not the same")
                    }
                }
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

        validateDataResponse(resGetComp, isPublic)

        logInfo("Created composition: ${resGetComp.toString()}", this::class.java)

        return res.data!!
    }

    suspend fun prepareComposition(): Pair<GridOneOffComposePrepared, Int> {
        val authorId = signupFlow.signupReturnId()
        val layoutId = spaceRepository.insertNewLayout(publicGridOneOffReq.name, authorId)

        val titlesOfImagesId =
            textRepository.batchInsertRecordsToNewCollection(publicGridOneOffReq.collectionOf_titles_of_image_categories)
        val image2dCollectionId =
            d2ImageRepository.batchInsertRecordsToNewCollection(publicGridOneOffReq.collectionOf_images_2d)
        val redirects2dCollectionId =
            d2TextRepository.batchInsertRecordsToNewCollection(publicGridOneOffReq.collectionOf_onclick_redirects)
        val imgDescriptions2dId =
            d2TextRepository.batchInsertRecordsToNewCollection(publicGridOneOffReq.collectionOf_img_descriptions)

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
            GridOneOffComposePrepared(
                sourceId = compositionSourceId,
                collectionOf_titles_of_image_categories_id = titlesOfImagesId,
                collectionOf_images_2d_id = image2dCollectionId,
                collectionOf_img_descriptions_id = imgDescriptions2dId,
                collectionOf_onclick_redirects_id = redirects2dCollectionId,
            ),
            compositionSourceId
        )
    }
}