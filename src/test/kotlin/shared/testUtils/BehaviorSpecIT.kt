package shared.testUtils

import configurations.AppEnv
import configurations.DIHelper
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.koin.KoinListener
import io.mockk.mockk
import org.apache.commons.mail.SimpleEmail
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import shared.DITestHelper

interface DoesHaveAppEnv : KoinTest {
    val appEnv: AppEnv
}

/** For integration tests to extend Koin, Kotest.BehaviorSpec, and extension functions. */
abstract class BehaviorSpecIT(body: BehaviorSpecIT.() -> Unit = {}) : BehaviorSpec(), KoinTest, DoesHaveAppEnv {
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

open class BehaviorSpecFlow(body: BehaviorSpecFlow.() -> Unit = {}) : KoinTest, DoesHaveAppEnv {
    override val appEnv: AppEnv by inject()

    init {
        body()
    }
}