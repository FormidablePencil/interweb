package com.idealIntent.managers

import com.idealIntent.configurations.AppEnv
import com.idealIntent.dtos.auth.TokenResponseData
import com.idealIntent.repositories.PasswordRepository
import com.idealIntent.repositories.RefreshTokenRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.http.*
import io.mockk.*
import models.authorization.IPasswordEntity
import org.mindrot.jbcrypt.BCrypt
import shared.appEnvMockHelper

class PasswordManagerTest : BehaviorSpec({
    val refreshTokenRepository: RefreshTokenRepository = mockk(relaxed = true)
    val passwordRepository: PasswordRepository = mockk(relaxed = true)
    val tokenManager: TokenManager = mockk()
    val appEnv: AppEnv = mockk()

    val authorId = 1
    val password = "an unencrypted password"
    val encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
    val mockedPasswordDb: IPasswordEntity = mockk()
    val tokens = TokenResponseData("access", "refresh")

    lateinit var passwordManager: PasswordManager


    beforeEach {
        clearAllMocks()

        passwordManager = spyk(PasswordManager(refreshTokenRepository, passwordRepository, tokenManager))

        appEnvMockHelper(appEnv, passwordManager)
        every { mockedPasswordDb.passwordHash } returns encryptedPassword
        every { passwordRepository.get(authorId) } returns mockedPasswordDb
        every { tokenManager.generateAuthTokens(authorId) } returns tokens
        every { passwordRepository.insert(any(), authorId) } returns true
        every { passwordRepository.delete(authorId) } returns true
        every { refreshTokenRepository.delete(authorId) } returns true
    }

    given("changePassword") {
        then("reset") {
            val res = passwordManager.changePassword("Current!123", "NewPass!123", authorId)

            verifyOrder {
                refreshTokenRepository.delete(authorId)
                passwordRepository.delete(authorId)
                passwordRepository.insert(any(), authorId)
                tokenManager.generateAuthTokens(authorId)
            }

            res.statusCode() shouldBe HttpStatusCode.OK
            res.data shouldNotBe null
        }
    }

    given("validatePassword") {

        then("valid password") {
            passwordManager.validatePassword(password, authorId) shouldBe true
        }
        then("invalid password") {
            passwordManager.validatePassword("invalidPassword", authorId) shouldBe false
        }
    }

    given("setNewPassword") {
        passwordManager.setNewPassword(encryptedPassword, authorId)
    }
})
