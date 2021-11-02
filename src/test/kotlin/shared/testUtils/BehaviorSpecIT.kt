package shared.testUtils

import configurations.DIHelper
import configurations.interfaces.IConnectionToDb
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
import org.koin.test.get
import shared.DITestHelper

interface DoHaveDbConnection {
    val connectionToDb: IConnectionToDb
}

/** For integration tests to extend Koin, Kotest.BehaviorSpec, and extension functions. */
open class BehaviorSpecIT(body: BehaviorSpecIT.() -> Unit = {}) : BehaviorSpec(), KoinTest, DoHaveDbConnection {
    override lateinit var connectionToDb: IConnectionToDb

    init {
        startKoin {
            modules(DIHelper.CoreModule, DITestHelper.FlowModule)
        }
        connectionToDb = get()
        body()
    }
}

suspend fun BehaviorSpecIT.whenUniqueConstraintDeprecated(constraintOn: String, code: suspend () -> Unit) {
    Given(constraintOn) {
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