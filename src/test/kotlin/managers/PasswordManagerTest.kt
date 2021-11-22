package managers

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import managers.interfaces.ITokenManager
import models.authorization.Password
import org.mindrot.jbcrypt.BCrypt
import repositories.interfaces.IPasswordRepository
import repositories.interfaces.IRefreshTokenRepository

class PasswordManagerTest : BehaviorSpec({
    val refreshTokenRepository: IRefreshTokenRepository = mockk()
    val passwordRepository: IPasswordRepository = mockk()
    val tokenManager: ITokenManager = mockk()

    val authorId = 1
    val password = "an unencrypted password"
    val encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
    val mockedPasswordDb: Password = mockk()

    beforeEach { // todo - add this to all the other tests I've missed to
        clearAllMocks()
    }

    given("resetPassword") { }

    given("validatePassword") {
        fun sharedEvery() {
            every { mockedPasswordDb.password } returns encryptedPassword
            every { passwordRepository.getPassword(authorId) } returns mockedPasswordDb
        }

        then("valid password") {
            sharedEvery()
            val passwordManager = PasswordManager(refreshTokenRepository, passwordRepository, tokenManager)
            passwordManager.validatePassword(password, authorId) shouldBe true
        }
        then("invalid password") {
            sharedEvery()
            val passwordManager = PasswordManager(refreshTokenRepository, passwordRepository, tokenManager)
            passwordManager.validatePassword("invalidPassword", authorId) shouldBe false
        }
    }

    given("setNewPassword") {
        every { passwordRepository.insertPassword(any(), authorId) } returns true

        val passwordManager = PasswordManager(refreshTokenRepository, passwordRepository, tokenManager)
        passwordManager.setNewPassword(encryptedPassword, authorId) shouldBe true
    }
})
