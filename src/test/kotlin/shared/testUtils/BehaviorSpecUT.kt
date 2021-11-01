package shared.testUtils

import io.kotest.core.spec.style.BehaviorSpec
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
import shared.DITestHelper

/** Inject required dependencies for unit tests. */
open class BehaviorSpecUT(body: BehaviorSpec.() -> Unit = {}) : BehaviorSpec(), KoinTest {
    init {
        startKoin {
            modules(DITestHelper.UnitTestModule)
        }
        body()
    }
}

