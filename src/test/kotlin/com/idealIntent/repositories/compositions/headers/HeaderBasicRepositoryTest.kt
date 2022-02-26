package com.idealIntent.repositories.compositions.headers

import com.idealIntent.configurations.DIHelper
import com.idealIntent.dtos.compositions.NewUserComposition
import com.idealIntent.dtos.compositions.headers.HeaderBasicCreateReq
import com.idealIntent.dtos.compositions.headers.HeaderBasicRes
import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.services.CompositionService
import dtos.compositions.CompositionCategory
import dtos.compositions.headers.CompositionHeader
import integrationTests.auth.flows.AuthUtilities
import integrationTests.auth.flows.SignupFlow
import integrationTests.compositions.headers.HeaderCompositionsFlow
import io.kotest.assertions.failure
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.IsolationMode
import io.kotest.koin.KoinListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import org.koin.core.component.inject
import shared.DITestHelper
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.rollback

class HeaderBasicRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(listOf(DIHelper.CoreModule, DITestHelper.FlowModule)))
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    private val spaceRepository: SpaceRepository by inject()
    private val headerBasicRepository: HeaderBasicRepository by inject()
    private val compositionPrivilegesManager: CompositionPrivilegesManager by inject()
    private val compositionService: CompositionService by inject()
    private val signupFlow: SignupFlow by inject()
    private val headerCompositionsFlow: HeaderCompositionsFlow by inject()

    private val userComposition = NewUserComposition(
        compositionCategory = CompositionCategory.Header,
        compositionType = CompositionHeader.Basic.value,
    )

    private suspend fun signup_then_createComposition(publicView: Boolean): Triple<Int, Int, Int> {
        val authorId = signupFlow.signupReturnId(AuthUtilities.createAuthorRequest)
        val layoutId = compositionService.createNewLayout(
            name = headerCompositionsFlow.layoutName, authorId = authorId
        ).data ?: throw failure("Failed to get id of newly created layout.")
        val compositionSourceId = headerCompositionsFlow.createComposition(publicView, layoutId, authorId)
        return Triple(compositionSourceId, layoutId, authorId)
    }

    init {
        beforeEach { clearAllMocks() }

        given("getOnlyTopLvlIdsOfCompositionOnlyModifiable") {

            then("Author id not privileged to view nor modify. Failed to retrieve private composition") {
                rollback {
                    val (compositionSourceId, _, authorId) = signup_then_createComposition(true)

                    headerBasicRepository.getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
                        compositionSourceId = compositionSourceId, authorId = 1
                    ) shouldBe null
                }
            }

            then("successfully retrieved private composition") {
                rollback {
                    val (compositionSourceId, _, authorId) = signup_then_createComposition(true)

                    headerBasicRepository.getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
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

                    headerBasicRepository.getPublicComposition(compositionSourceId = compositionSourceId) shouldBe null
                }
            }

            then("successfully got public composition") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) =
                        signup_then_createComposition(true)

                    val comp: HeaderBasicRes = headerBasicRepository.getPublicComposition(
                        compositionSourceId = compositionSourceId
                    ) ?: throw failure("failed to get composition")

                    // region verify
                    headerCompositionsFlow.let {
                        comp.bgImg shouldBe it.publicHeaderBasicReq.bgImg
                        comp.profileImg shouldBe it.publicHeaderBasicReq.profileImg
                        comp.name shouldBe it.publicHeaderBasicReq.name
                    }
                    // endregion
                }
            }
        }

        given("getPrivateComposition") {

            then("successfully got private composition") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) =
                        signup_then_createComposition(false)

                    val comp: HeaderBasicRes =
                        headerBasicRepository.getPrivateComposition(compositionSourceId, authorId)
                            ?: throw failure("failed to get composition")

                    // region verify
                    headerCompositionsFlow.let {
                        comp.bgImg shouldBe it.publicHeaderBasicReq.bgImg
                        comp.profileImg shouldBe it.publicHeaderBasicReq.profileImg
                        comp.name shouldBe it.publicHeaderBasicReq.name
                    }
                    // endregion
                }
            }
        }

        given("compose") {

            suspend fun prepareComposition(): Pair<HeaderBasicCreateReq, Int> {
                val authorId = signupFlow.signupReturnId()
                val createRequest = HeaderBasicCreateReq(
                    bgImg = "bg img",
                    profileImg = "profile img",
                    privilegedAuthors = listOf(),
                    name = "that was legitness",
                    privilegeLevel = 0,
                )
                val layoutId = spaceRepository.insertNewLayout(createRequest.name, authorId)

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

                return Pair(createRequest, compositionSourceId)
            }

            then("successfully composed collections and compositions as one composition") {
                rollback {
                    val (createRequest, compositionSourceId) = prepareComposition()

                    headerBasicRepository.compose(createRequest, compositionSourceId)
                }
            }
        }

        given("deleteComposition") {

            then("failed to delete because author id provided is not privileged to delete.") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) = signup_then_createComposition(true)

                    val ex = shouldThrowExactly<CompositionException> {
                        headerBasicRepository.deleteComposition(compositionSourceId, 999999999)
                    }.code shouldBe CompositionCode.CompositionNotFound
                }
            }

            then("failed to delete on an id of a composition that doesn't exist") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) = signup_then_createComposition(true)

                    val ex = shouldThrowExactly<CompositionException> {
                        headerBasicRepository.deleteComposition(999999999, authorId)
                    }.code shouldBe CompositionCode.CompositionNotFound
                }
            }

            then("success") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) = signup_then_createComposition(true)

                    // region before deletion assertions
                    val resBeforeDeletion: HeaderBasicRes = headerBasicRepository.getPublicComposition(
                        compositionSourceId = compositionSourceId
                    ) ?: throw failure("failed to get composition")

                    headerCompositionsFlow.let {
                        resBeforeDeletion.bgImg shouldBe it.publicHeaderBasicReq.bgImg
                        resBeforeDeletion.profileImg shouldBe it.publicHeaderBasicReq.profileImg
                        resBeforeDeletion.name shouldBe it.publicHeaderBasicReq.name
                    }
                    // endregion

                    headerBasicRepository.deleteComposition(compositionSourceId, authorId)

                    // region after deletion assertions
                    val resAfterDeletion = headerBasicRepository.getPublicComposition(compositionSourceId)

                    resAfterDeletion shouldBe null
                    // endregion
                }
            }
        }
    }
}