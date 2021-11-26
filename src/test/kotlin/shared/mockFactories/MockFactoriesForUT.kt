package shared.mockFactories

import com.typesafe.config.ConfigFactory
import configurations.AppEnv
import configurations.interfaces.IConnectionToDb
import io.ktor.config.*
import io.mockk.every
import io.mockk.mockk

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

fun appEnvMK(): AppEnv {
    val mock = mockk<AppEnv>()

    val configs = HoconApplicationConfig(ConfigFactory.load())

    every { mock.appConfig } returns configs

    return mock
}