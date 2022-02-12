package com.idealIntent.repositories.compositions

import com.idealIntent.configurations.DIHelper
import com.idealIntent.services.CompositionService
import integrationTests.compositions.flows.CompositionFlow
import io.kotest.core.spec.IsolationMode
import io.kotest.koin.KoinListener
import org.koin.core.component.inject
import shared.DITestHelper
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.rollback

class SpaceRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(listOf(DIHelper.CoreModule, DITestHelper.FlowModule)))
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest
    private val spaceRepository: SpaceRepository by inject()
    private val compositionService: CompositionService by inject()
    private val compositionFlow: CompositionFlow by inject()

    init {
        given("getLayoutMetadata") {
            then("success") {
                rollback {}
            }
        }

        given("insertNewLayout") {
            then("success") {
                rollback {}
            }
        }

        given("insertNewSpace") {
            then("success") {
                rollback {}
            }
        }

        given("associateCompositionToLayout") {

            then("provided a private layoutId") {
                rollback {}
            }

            then("success") {
                rollback {
                    val (compositionSourceId, layoutId, authorId) =
                        compositionFlow.signup_then_createComposition(false)

                    val vd = spaceRepository.associateCompositionToLayout(
                        220000, compositionSourceId, layoutId
                    )
                }
            }
        }

        given("validateAuthorPrivilegedToModify") {
            then("success") {
                rollback {}
            }
        }

        given("associateLayoutToSpace") {
            then("success") {
                rollback {}
            }
        }

        given("giveAuthorPrivilegesToPrivateSpace") {
            then("success") {
                rollback {}
            }
        }

        given("getSpace") {
            then("success") {
                rollback {}
            }
        }

        given("insertSpace") {
            then("success") {
                rollback {}
            }
        }

        given("getSpaceLayoutOfCompositions") {
            then("success") {
                // region setup
                // todo - create compositions, associate them to layout, associate layout to space
//                cmsService.createComposition()
                // endregion
                //  then get compositions of space.
            }
        }

    }
}
