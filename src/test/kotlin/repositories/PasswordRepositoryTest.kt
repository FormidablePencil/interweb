package repositories

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.koin.test.inject
import org.mindrot.jbcrypt.BCrypt
import shared.persistentId
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.rollback
import shared.testUtils.whenUniqueConstraintDeprecated

class PasswordRepositoryTest : BehaviorSpecUtRepo({
    val passwordRepository: PasswordRepository by inject()
    val password = "StrongPassword!123"
    val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())
    val authorId = persistentId

    // todo - how are we going to go about testing repositories?
    xgiven("insertPassword()") {
        rollback {
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
    }

    given("constraints") {
        whenUniqueConstraintDeprecated("author_id") {
            passwordRepository.insertPassword(passwordHash, 2) shouldBe true
        }
    }
})
