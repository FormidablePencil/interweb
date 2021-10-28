package shared

import configurations.IConnectionToDb
import io.kotlintest.specs.*
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
            var result = code()
            connectionToDb.database.transactionManager.currentTransaction?.rollback()
            return CleanupResult(result);
        }
    else {
        var result = code()
        return CleanupResult(result);
    }
}

open class KoinBehaviorSpec : BehaviorSpec(), KoinTest, ICleanupTest {
    override val connectionToDb: IConnectionToDb by inject()
}

open class UtBehaviorSpec : BehaviorSpec(), KoinTest { init { startUt() } }

fun startUt() {
    startKoin {
        modules(DITestHelper.UnitTestModule)
    }
}
