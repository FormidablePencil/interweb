package shared.testUtils

import com.idealIntent.configurations.DIHelper
import io.kotest.core.spec.IsolationMode
import io.kotest.koin.KoinListener
import shared.DITestHelper

/** Unit testing com.idealIntent.repositories class. */
abstract class BehaviorSpecUtRepo(body: BehaviorSpecUtRepo.() -> Unit = {}) : BehaviorSpecIT(), SqlColConstraint {
    init {
        body()
    }
}

abstract class BehaviorSpecUtRepo2(body: BehaviorSpecUtRepo2.() -> Unit = {}) : BehaviorSpecIT(), SqlColConstraint {
    override fun listeners() = listOf(KoinListener(listOf(DIHelper.CoreModule, DITestHelper.FlowModule)))
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    init {
        body()
    }
}
