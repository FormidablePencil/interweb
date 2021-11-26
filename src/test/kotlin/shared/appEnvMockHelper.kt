package shared

import com.typesafe.config.ConfigFactory
import configurations.AppEnv
import io.ktor.config.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot

fun appEnvMockHelper(appEnv: AppEnv, dependency: Any? = null) {
    val observerMock = mockk<(f: Any) -> Unit>()
    every { appEnv.database.useTransaction { transaction -> observerMock(transaction) } }

    val configs = HoconApplicationConfig(ConfigFactory.load())
    val capturedPath = slot<String>()
    every { appEnv.getConfig(capture(capturedPath)) } answers { configs.property(capturedPath.captured).getString() }

    if (dependency != null) every { dependency getProperty "appEnv" } returns appEnv
}