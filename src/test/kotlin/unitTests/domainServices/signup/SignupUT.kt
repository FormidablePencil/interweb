package unitTests.domainServices.signup

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

//    every { authorizationService.generateTokens(any()) } returns TokensResponse("", "")

//    every { authorizationService.setNewPasswordForSignup(any(), tokens) } returns passwordId

    every { authorRepository.getByEmail(any()) } returns null
    every { authorRepository.getByUsername(any()) } returns null
    every { authorRepository.insertAuthor(any()) } returns 123



})