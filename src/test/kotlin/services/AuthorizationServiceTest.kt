package services

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import managers.interfaces.IEmailManager
import managers.interfaces.IPasswordManager
import managers.interfaces.ITokenManager
import models.profile.Author
import repositories.interfaces.IAuthorRepository
import repositories.interfaces.IRefreshTokenRepository

class AuthorizationServiceTest : BehaviorSpec({
    val authorRepository: IAuthorRepository = mockk()
    val tokenManager: ITokenManager = mockk()
    val refreshTokenRepository: IRefreshTokenRepository = mockk()
    val emailService: IEmailManager = mockk()
    val passwordManager: IPasswordManager = mockk()
    val username = "username"
    val email = "email"
    val authorForUsername = Author { val id = 1 }
    val authorForEmail = Author { val id = 2 }

    every { authorRepository.getByUsername(username) } returns authorForUsername
    every { authorRepository.getByUsername(email) } returns authorForEmail
    every { emailService.sendResetPassword(authorForUsername.id) }

    val authorizationService = AuthorizationService(
        authorRepository, tokenManager, emailService, passwordManager
    )

    given("login") { }

    given("refreshAccessToken") { }

    given("requestPasswordReset") {
        and("valid username and email") {
            val result = authorizationService.requestPasswordReset(username, email)
        }
    }

    given("resetPasswordByEmail") { }
})
