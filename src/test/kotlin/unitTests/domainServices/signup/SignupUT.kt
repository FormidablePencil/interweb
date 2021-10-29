package unitTests.domainServices.signup

import dtos.author.CreateAuthorRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import managers.interfaces.ITokenManager
import models.profile.Author
import repositories.interfaces.IAuthorRepository
import services.AuthorizationService
import shared.BehaviorSpecUT

class SignupUT : BehaviorSpecUT({
    lateinit var authorizationService: AuthorizationService
    var tokenManager: ITokenManager = mockk()
    var authorRepository: IAuthorRepository = mockk()
    val authorId = 1
    val passwordId = 2
    val fakeAuthor = Author {
        val id = 3;
        val email = "email"
    }

//    every { authorizationService.generateTokens(any()) } returns TokensResult("", "")

    every { authorizationService.setNewPasswordForSignup(any()) } returns passwordId

    every { authorRepository.getByEmail(any()) } returns null
    every { authorRepository.getByUsername(any()) } returns null
    every { authorRepository.createAuthor(any()) } returns authorId



})