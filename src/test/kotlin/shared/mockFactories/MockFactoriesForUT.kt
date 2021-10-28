package shared.mockFactories

import com.typesafe.config.ConfigFactory
import configurations.IAppEnv
import configurations.IConnectionToDb
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

fun appEnvMK(): IAppEnv {
    val mock = mockk<IAppEnv>()

    every { mock.appConfig } returns HoconApplicationConfig(ConfigFactory.load("application.conf"))

    return mock
}
