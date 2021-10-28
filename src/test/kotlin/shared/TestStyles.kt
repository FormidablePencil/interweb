package shared

import configurations.DIHelper
import configurations.IConnectionToDb
import io.kotest.core.spec.style.BehaviorSpec
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.test.KoinTest

data class CleanupResult<T>(val value: T)

interface ICleanupTest {
    val connectionToDb: IConnectionToDb
}

fun <T> ICleanupTest.cleanup(cleanup: Boolean, code: () -> T): CleanupResult<T> {
    if (cleanup)
        connectionToDb.database.useTransaction {
            val result = code()
            connectionToDb.database.transactionManager.currentTransaction?.rollback()
            return CleanupResult(result);
        }
    else {
        val result = code()
        return CleanupResult(result);
    }
}

open class BehaviorSpecFlowsForIntegrationTests(body: BehaviorSpecFlowsForIntegrationTests.() -> Unit = {}) : BehaviorSpec(), KoinTest, ICleanupTest {
    override val connectionToDb: IConnectionToDb by inject()

    init {
        startKoin {
            modules(DIHelper.CoreModule, DITestHelper.FlowModule)
        }
        body()
    }
}

open class BehaviorSpecIT(body: BehaviorSpecFlowsForIntegrationTests.() -> Unit = {}) : BehaviorSpecFlowsForIntegrationTests(body)

// There a few places where there are dependency inject not through constructor but directly in class. This is
// where you'll have to use koin to dependency inject mocked version. Otherwise, use BehaviorSpec instead
open class BehaviorSpecUT(body: BehaviorSpec.() -> Unit = {}) : BehaviorSpec(), KoinTest {
    init {
        startKoin {
            modules(DITestHelper.UnitTestModule)
        }
        body()
    }
}
