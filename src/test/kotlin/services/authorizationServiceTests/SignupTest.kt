package services.authorizationServiceTests

import dtos.signup.SignupResponseFailed
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.mockk.*
import managers.interfaces.IEmailManager
import managers.interfaces.IPasswordManager
import managers.interfaces.ITokenManager
import models.profile.Author
import repositories.interfaces.IAuthorRepository
import repositories.interfaces.IEmailVerifyCodeRepository
import serialized.CreateAuthorRequest
import serialized.TokenResponseData
import services.AuthorizationService
import shared.testUtils.BehaviorSpecUT

class SignupTest : BehaviorSpecUT({

    val authorRepository: IAuthorRepository = mockk()
    val tokenManager: ITokenManager = mockk()
    val emailManager: IEmailManager = mockk()
    val passwordManager: IPasswordManager = mockk()
    val emailVerifyCodeRepository: IEmailVerifyCodeRepository = mockk()
    val author: Author = mockk()

    val username = "YourNeighborhoodSpider"
    val email = "testemail12345@gmail.com"
    val password = "Formidable!76"
    val authorForUsername = Author { val id = 1 }
    val authorForEmail = Author { val id = 2 }
    val tokenResponseData = TokenResponseData("access token", "refresh token")

    val authorizationService =
        AuthorizationService(authorRepository, tokenManager, emailManager, passwordManager, emailVerifyCodeRepository)

    beforeEach {
        clearAllMocks()
    }


})
