package shared.testUtils

import configurations.AppEnv
import configurations.DIHelper
import integrationTests.auth.flows.LoginFlow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.koin.KoinListener
import io.mockk.mockk
import org.apache.commons.mail.SimpleEmail
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import shared.DITestHelper

/** For integration tests to extend Koin, Kotest.BehaviorSpec, and extension functions. */
abstract class BehaviorSpecIT(body: BehaviorSpecIT.() -> Unit = {}) : BehaviorSpec(), KoinTest, DoHaveDbConnection {
    override val appEnv: AppEnv by inject()

    override fun listeners() = listOf(
        KoinListener(
            listOf(
                DIHelper.CoreModule,
                DITestHelper.FlowModule,
                module {
                    single { mockk<SimpleEmail>(relaxed = true) }
                }
            )
        )
    )

    init {
        body()
    }
}

interface DoHaveDbConnection : KoinTest {
    val appEnv: AppEnv
}

open class BehaviorSpecFlow(body: BehaviorSpecFlow.() -> Unit = {}) : KoinTest, DoHaveDbConnection {
    override val appEnv: AppEnv by inject()

    init {
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