package shared

import configurations.DIHelper
import configurations.IConnectionToDb
import io.kotlintest.specs.*
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.test.KoinTest

data class CleanupResult<T>(val value: T)

open class KoinBehaviorSpec : BehaviorSpec(), KoinTest {
    private val connectionToDb: IConnectionToDb by inject()

    fun <T> cleanup(cleanup: Boolean, code: () -> T): CleanupResult<T> {
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

    init {
        startKoin {
            modules(DIHelper.CoreModule, DITestHelper.CoreModule)
        }
    }
}

fun startUt() {
    startKoin {
        modules(DITestHelper.UnitTestModule)
    }
}

open class UtFeatureSpec : FeatureSpec(), KoinTest { init {
    startUt()
}
}

open class UtBehaviorSpec : BehaviorSpec(), KoinTest { init {
    startUt()
}
}

open class UtFreeSpec : FreeSpec(), KoinTest { init {
    startUt()
}
}

open class UtDescribeSpec : DescribeSpec(), KoinTest { init {
    startUt()
}
}

open class UtExpectSpec : ExpectSpec(), KoinTest { init {
    startUt()
}
}