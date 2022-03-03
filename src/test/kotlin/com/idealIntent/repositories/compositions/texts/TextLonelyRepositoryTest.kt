package com.idealIntent.repositories.compositions.texts

import com.idealIntent.configurations.DIHelper
import com.idealIntent.dtos.compositions.NewUserComposition
import com.idealIntent.dtos.compositions.texts.TextLonelyRes
import com.idealIntent.managers.CompositionPrivilegesManager
import com.idealIntent.repositories.compositions.SpaceRepository
import com.idealIntent.services.CompositionService
import dtos.compositions.CompositionCategory
import dtos.compositions.headers.CompositionHeader
import integrationTests.auth.flows.SignupFlow
import integrationTests.compositions.texts.TextCompositionsFlow
import integrationTests.compositions.texts.TextCompositionsFlow.Companion.privateTextLonelyCreateReq
import integrationTests.compositions.texts.TextCompositionsFlow.Companion.publicTextLonelyCreateReq
import io.kotest.assertions.failure
import io.kotest.core.spec.IsolationMode
import io.kotest.koin.KoinListener
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import org.koin.core.component.inject
import shared.DITestHelper
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.rollback

class TextLonelyRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(listOf(DIHelper.CoreModule, DITestHelper.FlowModule)))
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    private val spaceRepository: SpaceRepository by inject()
    private val textLonelyRepository: TextLonelyRepository by inject()
    private val compositionPrivilegesManager: CompositionPrivilegesManager by inject()
    private val compositionService: CompositionService by inject()
    private val signupFlow: SignupFlow by inject()
    private val textCompositionsFlow: TextCompositionsFlow by inject()

    private val userComposition = NewUserComposition(
        compositionCategory = CompositionCategory.Header,
        compositionType = CompositionHeader.Basic.value,
    )

    private suspend fun signup_then_createComposition(isPublic: Boolean): Triple<Int, Int, Int> {
        val authorId = signupFlow.signupReturnId()
        val layoutId = compositionService.createNewLayout(
            name = TextCompositionsFlow.layoutName, authorId = authorId
        ).data ?: throw failure("Failed to get id of newly created layout.")
        val compositionSourceId = textCompositionsFlow.createComposition(isPublic, layoutId, authorId)
        return Triple(compositionSourceId, layoutId, authorId)
    }

    init {
        beforeEach { clearAllMocks() }

        given("getPublicComposition") {

            then("failed to get because composition is private") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) =
                        signup_then_createComposition(false)

                    textLonelyRepository.getPublicComposition(compositionSourceId = compositionSourceId) shouldBe null
                }
            }

            then("successfully got public composition") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) = signup_then_createComposition(true)

                    val comp: TextLonelyRes = textLonelyRepository.getPublicComposition(
                        compositionSourceId = compositionSourceId
                    ) ?: throw failure("failed to get composition")

                    // region verify
                    publicTextLonelyCreateReq.let {
                        comp.name shouldBe it.name
                        comp.text shouldBe it.text
                        comp.privilegeLevel shouldBe it.privilegeLevel
                    }
                    // endregion
                }
            }
        }

        given("getPrivateComposition") {

            then("successfully got private composition") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) = signup_then_createComposition(false)

                    val res: TextLonelyRes = textLonelyRepository.getPrivateComposition(
                        compositionSourceId, authorId
                    ) ?: throw failure("failed to get composition")

                    TextCompositionsFlow.validateDataResponse(res)
                }
            }
        }

        given("compose") {

            then("successfully composed collections and compositions as one composition") {
                rollback {
                    val (composePrepared, compositionSourceId) = textCompositionsFlow.prepareComposition(
                        privateTextLonelyCreateReq
                    )

                    textLonelyRepository.compose(composePrepared, compositionSourceId)
                }
            }
        }

        xgiven("deleteComposition") {
//
//            then("failed to delete because author id provided is not privileged to delete.") {
//                rollback {
//                    val (compositionSourceId, layoutId, authorId) = signup_then_createComposition(true)
//
//                    val ex = shouldThrowExactly<CompositionException> {
//                        textLonelyRepository.deleteComposition(compositionSourceId, 999999999)
//                    }.code shouldBe CompositionCode.CompositionNotFound
//                }
//            }
//
//            then("failed to delete on an id of a composition that doesn't exist") {
//                rollback {
//                    val (compositionSourceId, layoutId, authorId) = signup_then_createComposition(true)
//
//                    val ex = shouldThrowExactly<CompositionException> {
//                        textLonelyRepository.deleteComposition(999999999, authorId)
//                    }.code shouldBe CompositionCode.CompositionNotFound
//                }
//            }
//
//            then("success") {
//                rollback {
//                    val (compositionSourceId, layoutId, authorId) = signup_then_createComposition(true)
//
//                    // region before deletion assertions
//                    val resBeforeDeletion:  TextLonelyRes = textLonelyRepository.getPublicComposition(
//                        compositionSourceId = compositionSourceId
//                    ) ?: throw failure("failed to get composition")
//
//                    HeaderCompositionsFlow.publicHeaderBasicReq.let {
//                        resBeforeDeletion.bgImg shouldBe it.bgImg
//                        resBeforeDeletion.profileImg shouldBe it.profileImg
//                        resBeforeDeletion.name shouldBe it.name
//                    }
//                    // endregion
//
//                    textLonelyRepository.deleteComposition(compositionSourceId, authorId)
//
//                    // region after deletion assertions
//                    val resAfterDeletion = textLonelyRepository.getPublicComposition(compositionSourceId)
//
//                    resAfterDeletion shouldBe null
//                    // endregion
//                }
//            }
        }
    }
}