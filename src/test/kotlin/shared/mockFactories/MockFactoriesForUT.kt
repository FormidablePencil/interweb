package shared.mockFactories

import com.typesafe.config.ConfigFactory
import configurations.interfaces.IAppEnv
import configurations.interfaces.IConnectionToDb
import io.ktor.config.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot

fun connectionToDbMK(): IConnectionToDb {
    val mock = mockk<IConnectionToDb>(relaxed = true)
    val observerMock = mockk<(f: Any) -> Unit>()

    every {
        mock.database.useTransaction { transaction ->
            observerMock(transaction)
        }
    }

    return mock
}

fun appEnvMK(): IAppEnv {
    val mock = mockk<IAppEnv>()

    val configs = HoconApplicationConfig(ConfigFactory.load())

    every { mock.appConfig } returns configs

    return mock
}