package shared.mockFactories

import dto.author.CreateAuthorRequest
import helper.DbHelper
import io.mockk.every
import io.mockk.mockk
import managers.AuthorizationManager
import managers.IAuthorizationManager
import managers.ITokenManager
import models.Author
import repositories.AuthorRepository
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

fun dbHelperMK(): DbHelper {
    val mock = mockk<DbHelper>()

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
