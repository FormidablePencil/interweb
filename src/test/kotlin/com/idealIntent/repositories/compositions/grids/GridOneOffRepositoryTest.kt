package com.idealIntent.repositories.compositions.grids

import com.idealIntent.configurations.DIHelper
import com.idealIntent.dtos.compositions.grids.GridOneOffRes
import com.idealIntent.services.CompositionService
import integrationTests.auth.flows.AuthUtilities
import integrationTests.auth.flows.SignupFlow
import integrationTests.compositions.grids.GridCompositionsFlow
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

    private val gridOneOffRepository: GridOneOffRepository by inject()
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

        given("getPublicComposition") {

            then("failed to get because composition is private") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) =
                        signup_then_createComposition(false)

                    gridOneOffRepository.getPublicComposition(compositionSourceId = compositionSourceId) shouldBe null
                }
            }

            then("successfully got public composition") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) =
                        signup_then_createComposition(true)

                    val res: GridOneOffRes = gridOneOffRepository.getPublicComposition(
                        compositionSourceId = compositionSourceId
                    ) ?: throw failure("failed to get composition")

                    GridCompositionsFlow.validateDataResponse(res, true)
                }
            }
        }

        given("getPrivateComposition") {

            then("successfully got private composition") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) =
                        signup_then_createComposition(false)

                    val res: GridOneOffRes = gridOneOffRepository.getPrivateComposition(compositionSourceId, authorId)
                        ?: throw failure("failed to get composition")

                    GridCompositionsFlow.validateDataResponse(res, false)
                }
            }
        }

        given("compose") {

            then("successfully composed collections and compositions as one composition") {
                rollback {
                    val (preparedComposition, sourceId) = gridCompositionsFlow.prepareComposition()

                    gridOneOffRepository.compose(preparedComposition, sourceId)
                }
            }
        }

    }
}