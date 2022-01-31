package com.idealIntent.repositories

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.koin.test.inject
import org.mindrot.jbcrypt.BCrypt
import com.idealIntent.repositories.profile.AuthorProfileRelatedRepository
import com.idealIntent.dtos.CreateAuthorRequest
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.SqlConstraint
import shared.testUtils.rollback

class PasswordRepositoryTest : BehaviorSpecUtRepo({
    val passwordRepository: PasswordRepository by inject()
    val authorProfileRelatedRepository: AuthorProfileRelatedRepository by inject()
    val password = "StrongPassword!123"
    val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())

    fun createAuthor(): Int {
        return authorProfileRelatedRepository.createNewAuthor(
            CreateAuthorRequest(
                "someKind@asd.cof", "first", "last", "!Password123", "user"
            )
        )
            ?: throw Exception("failed to create new author")
    }

    given("insert, get, deletePassword") {
        then("work properly") {
            rollback {
                val authorId = createAuthor()

                passwordRepository.insert(passwordHash, authorId) shouldBe true

                val getPasswordRes = passwordRepository.get(authorId)
                    ?: throw Exception("test failed")
                getPasswordRes.passwordHash shouldNotBe password
                BCrypt.checkpw(password, getPasswordRes.passwordHash) shouldBe true

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
