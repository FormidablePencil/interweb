package com.idealIntent.repositories.compositions.grids

import com.idealIntent.configurations.DIHelper
import com.idealIntent.dtos.compositions.grids.GridOneOffComposePrepared
import com.idealIntent.dtos.compositions.grids.GridOneOffCreateReq
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.managers.compositions.images.D2ImageRepository
import com.idealIntent.managers.compositions.texts.D2TextRepository
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import com.idealIntent.repositories.collectionsGeneric.TextRepository
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.services.CompositionService
import integrationTests.auth.flows.AuthUtilities
import integrationTests.auth.flows.SignupFlow
import integrationTests.compositions.grids.GridCompositionsFlow
import integrationTests.compositions.grids.GridCompositionsFlow.Companion.publicGridOneOffReq
import io.kotest.assertions.failure
import io.kotest.core.spec.IsolationMode
import io.kotest.koin.KoinListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import org.koin.core.component.inject
import shared.DITestHelper
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.rollback

class GridOneOffRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(listOf(DIHelper.CoreModule, DITestHelper.FlowModule)))
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    private val imageRepository: ImageRepository by inject()
    private val textRepository: TextRepository by inject()
    private val d2ImageRepository: D2ImageRepository by inject()
    private val d2TextRepository: D2TextRepository by inject()
    private val spaceRepository: SpaceRepository by inject()
    private val gridOneOffRepository: GridOneOffRepository by inject()
    private val compositionPrivilegesManager: CompositionPrivilegesManager by inject()
    private val compositionService: CompositionService by inject()
    private val signupFlow: SignupFlow by inject()
    private val gridCompositionsFlow: GridCompositionsFlow by inject()

    private suspend fun signup_then_createComposition(publicView: Boolean): Triple<Int, Int, Int> {
        val authorId = signupFlow.signupReturnId(AuthUtilities.createAuthorRequest)
        val layoutId = compositionService.createNewLayout(
            name = gridCompositionsFlow.layoutName, authorId = authorId
        ).data ?: throw failure("Failed to get id of newly created layout.")
        val compositionSourceId = gridCompositionsFlow.createComposition(publicView, layoutId, authorId)
        return Triple(compositionSourceId, layoutId, authorId)
    }

    init {
        beforeEach { clearAllMocks() }

        given("getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify") {

            then("Author id not privileged to view nor modify. Failed to retrieve private composition") {
                rollback {
                    val (compositionSourceId, _, authorId) = signup_then_createComposition(true)

                    gridOneOffRepository.getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
                        compositionSourceId = compositionSourceId, authorId = 1
                    ) shouldBe null
                }
            }

            then("successfully retrieved private composition") {
                rollback {
                    val (compositionSourceId, _, authorId) = signup_then_createComposition(true)

                    gridOneOffRepository.getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
                        compositionSourceId = compositionSourceId, authorId = authorId
                    ) shouldNotBe null
                }
            }
        }

        given("compose") {

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

                val composePrepared = GridOneOffComposePrepared(
                    collectionOf_titles_of_image_categories_id = titlesOfImagesId,
                    collectionOf_images_2d_id = image2dCollectionId,
                    collectionOf_img_descriptions_id = imgDescriptions2dId,
                    collectionOf_onclick_redirects_id = redirects2dCollectionId,
                )

                return Pair(composePrepared, compositionSourceId)
            }

            then("successfully composed collections and compositions as one composition") {
                rollback {
                    val (composePrepared, compositionSourceId) = prepareComposition()

                    gridOneOffRepository.compose(composePrepared, compositionSourceId)
                }
            }
        }

    }
}