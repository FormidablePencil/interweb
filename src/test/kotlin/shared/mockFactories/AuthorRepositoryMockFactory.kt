package shared.mockFactories

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
