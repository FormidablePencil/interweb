package shared.testUtils

import configurations.DIHelper
import configurations.interfaces.IConnectionToDb
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.scopes.BehaviorSpecGivenContainerContext
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
import org.koin.test.get
import shared.DITestHelper

/** For integration tests to extend Koin, Kotest.BehaviorSpec, and extension functions. */
open class BehaviorSpecIT(body: BehaviorSpecIT.() -> Unit = {}) : BehaviorSpec(), KoinTest, IRollback {
    final override var connectionToDb: IConnectionToDb? = null

    init {
        startKoin {
            modules(DIHelper.CoreModule, DITestHelper.FlowModule)
        }
        connectionToDb = get()
        body()
    }
}

/** Rollback after test has run. */
fun BehaviorSpecIT.rollbackGiven(name: String, code: suspend BehaviorSpecGivenContainerContext.() -> Unit) {
    given(name) {
        rollbackSuspend {
            code()
        }
    }
}

/** If database was wiped the insertion will succeed therefore run the insertion again to test that it failed. */
fun BehaviorSpecIT.testDupGiven(name: String, code: suspend BehaviorSpecGivenContainerContext.() -> Unit) {
    given(name) {
        shouldThrow<Exception> {
            try {
                code()
            } catch (ex: Exception) {
                throw Exception(ex)
            }
            try {
                code()
            } catch (ex: Exception) {
                throw Exception(ex)
            }
        }
    }
}