package com.idealIntent.repositories.compositions

import com.idealIntent.configurations.DIHelper
import com.idealIntent.services.CmsService
import io.kotest.core.spec.IsolationMode
import io.kotest.koin.KoinListener
import org.koin.core.component.inject
import shared.DITestHelper
import shared.testUtils.BehaviorSpecUtRepo

class SpaceRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(listOf(DIHelper.CoreModule, DITestHelper.FlowModule)))
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest
    private val spaceRepository: SpaceRepository by inject()
    private val cmsService: CmsService by inject()

    init {
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
