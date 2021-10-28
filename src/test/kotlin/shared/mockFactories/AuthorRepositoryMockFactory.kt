package shared.mockFactories

import com.typesafe.config.ConfigFactory
import configurations.AppEnv
import configurations.IAppEnv
import configurations.IConnectionToDb
import io.ktor.config.*
import io.mockk.every
import io.mockk.mockk
import managers.IAuthorizationManager
import managers.ITokenManager
import models.Author
import repositories.IAuthorRepository

fun authorRepositoryMK(): IAuthorRepository {
    val mock = mockk<IAuthorRepository>()
    val fakeAuthor = Author {
        val id = 1
        val email = "email"
    }
    every { mock.getByEmail(any()) } returns fakeAuthor
    every { mock.getByUsername(any()) } returns fakeAuthor
    every { mock.createAuthor(any()) } returns 2133
    return mock
}

fun authorizationManagerMK(): IAuthorizationManager {
    val mock = mockk<IAuthorizationManager>()

    return mock
}

fun tokenManagerMK(): ITokenManager {
    val mock = mockk<ITokenManager>()

    return mock
}


fun connectionToDbMK(): IConnectionToDb {
    val mock = mockk<IConnectionToDb>(relaxed = true)
    val observerMock = mockk<(f: Any) -> Unit>()

    every { mock.database.useTransaction {
    transaction -> observerMock(transaction)
    } }

    return mock
}


fun appEnvMK(): IAppEnv {
    val mock = mockk<IAppEnv>()

    every { mock.appConfig } returns HoconApplicationConfig(ConfigFactory.load("application.conf"))

    return mock
}
