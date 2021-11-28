package repositories

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.koin.test.inject
import org.mindrot.jbcrypt.BCrypt
import serialized.CreateAuthorRequest
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.SqlConstraint
import shared.testUtils.rollback

class PasswordRepositoryTest : BehaviorSpecUtRepo({
    val passwordRepository: PasswordRepository by inject()
    val authorRepository: AuthorRepository by inject()
    val password = "StrongPassword!123"
    val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())

    fun createAuthor(): Int {
        return authorRepository.insert(
            CreateAuthorRequest(
                "someKind@asd.cof", "first", "last", "!Password123", "user"
            )
        )
            ?: throw Exception("failed to insert")
    }

    given("insert, get, deletePassword") {
        then("work properly") {
            rollback {
                val authorId = createAuthor()

                passwordRepository.insert(passwordHash, authorId) shouldBe true

                val getPasswordRes = passwordRepository.get(authorId)
                    ?: throw Exception("test failed")
                getPasswordRes.password shouldNotBe password
                BCrypt.checkpw(password, getPasswordRes.password) shouldBe true

                passwordRepository.delete(authorId) shouldBe true
                passwordRepository.get(authorId) shouldBe null
            }
        }
    }

    given("constraints") {
        whenConstraint(SqlConstraint.Unique, "author_id") {
            val authorId = createAuthor()

            passwordRepository.insert(passwordHash, authorId) shouldBe true
            // todo - Passwords has a foreign key and so a valid author_id must be provided but if it's not then the whenConstraint should fail, but it does not
        }
    }
})
