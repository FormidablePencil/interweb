package dtos.

import com.idealIntent.configurations.DIHelper
import io.kotest.koin.KoinListener
import io.kotest.matchers.shouldBe
import com.idealIntent.repositories.codes.EmailVerificationCodeRepository
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.rollback

class EmailVerificationCodeRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(DIHelper.CoreModule))

    init {
        lateinit var emailVerificationCodeRepository: EmailVerificationCodeRepository

        beforeEach {
            emailVerificationCodeRepository = EmailVerificationCodeRepository()
        }

        given("insert, get, getCode, delete") {
            then("validates") {
                rollback {
                    val authorId = 54321
                    val code = "code-blue"
                    emailVerificationCodeRepository.insert(code, authorId) shouldBe true
                    emailVerificationCodeRepository.getCode(authorId) shouldBe code
                    emailVerificationCodeRepository.delete(authorId)
                    emailVerificationCodeRepository.get(authorId) shouldBe null
                }
            }
        }
    }
}