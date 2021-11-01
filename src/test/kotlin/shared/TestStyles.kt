package shared

import com.mysql.cj.x.protobuf.MysqlxResultset
import configurations.DIHelper
import configurations.interfaces.IConnectionToDb
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.scopes.BehaviorSpecGivenContainerContext
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
import org.koin.test.get

data class CleanupResult<T>(val value: T)

interface ICleanupTest {
    val connectionToDb: IConnectionToDb?
}

/** Rollback transaction wrapper for repository unit tests which call the database and integration tests. One limitation
 *  is you cannot test inserting duplicate data because of how db transactions work. */
suspend fun <T> ICleanupTest.cleanupGiven(cleanup: Boolean = true, code: suspend () -> T): CleanupResult<T> {
    connectionToDb ?: throw Exception("cleanup fun failed")
    if (cleanup)
        connectionToDb!!.database.useTransaction {
            val result = code()
            connectionToDb!!.database.transactionManager.currentTransaction?.rollback()
            return CleanupResult(result);
        }
    else {
        val result = code()
        return CleanupResult(result);
    }
}

/** Rollback transaction wrapper for integration testing. */
fun <T> ICleanupTest.cleanup(cleanup: Boolean = true, code: () -> T): CleanupResult<T> {
    connectionToDb ?: throw Exception("cleanup fun failed")
    if (cleanup)
        connectionToDb!!.database.useTransaction {
            val result = code()
            connectionToDb!!.database.transactionManager.currentTransaction?.rollback()
            return CleanupResult(result);
        }
    else {
        val result = code()
        return CleanupResult(result);
    }
}

/** For integration test to extend Koin, Kotest.BehaviorSpec, and extension functions. */
open class BehaviorSpecIT(body: BehaviorSpecIT.() -> Unit = {}) : BehaviorSpec(), KoinTest, ICleanupTest {
    final override var connectionToDb: IConnectionToDb? = null

    init {
        startKoin {
            modules(DIHelper.CoreModule, DITestHelper.FlowModule)
        }
        connectionToDb = get()
        body()
    }
}

typealias BehaviorSpecUtRepo = BehaviorSpecIT

fun BehaviorSpecIT.rollbackGiven(name: String, codeHere: suspend BehaviorSpecGivenContainerContext.() -> Unit) {
    given(name) {
        cleanupGiven {
            codeHere()
        }
    }
}

open class BehaviorSpecUT(body: BehaviorSpec.() -> Unit = {}) : BehaviorSpec(), KoinTest {
    init {
        startKoin {
            modules(DITestHelper.UnitTestModule)
        }
        body()
    }
}

