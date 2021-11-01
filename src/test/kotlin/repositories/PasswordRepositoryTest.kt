package repositories

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.koin.test.get
import org.mindrot.jbcrypt.BCrypt
import repositories.interfaces.IPasswordRepository
import shared.testUtils.BehaviorSpecUtRepo
import shared.persistentId
import shared.testUtils.rollbackGiven

class PasswordRepositoryTest : BehaviorSpecUtRepo({
    val passwordRepository: IPasswordRepository = get()
    val password = "StrongPassword!123"
    val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())
    val authorId = persistentId

    rollbackGiven("insertPassword()") {
        passwordRepository.insertPassword(passwordHash, authorId) shouldBe true

        then("getPassword()") {
            val getPasswordRes = passwordRepository.getPassword(authorId)
                ?: throw Exception("test failed")
            getPasswordRes.password shouldNotBe password
            BCrypt.checkpw(password, getPasswordRes.password) shouldBe true
        }

        then("deletePassword()") {
            passwordRepository.deletePassword(authorId) shouldBe true
        }

    }
})
