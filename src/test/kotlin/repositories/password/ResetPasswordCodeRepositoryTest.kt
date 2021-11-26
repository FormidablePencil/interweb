package repositories.password

import configurations.DIHelper
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.koin.KoinListener
import io.kotest.matchers.shouldBe
import repositories.EmailVerificationCodeRepository
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.rollback

class ResetPasswordCodeRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(DIHelper.CoreModule))

    init {
        lateinit var resetPasswordCodeRepository: ResetPasswordCodeRepository

        beforeEach {
            resetPasswordCodeRepository = ResetPasswordCodeRepository()
        }

        given("insert, get, getCode, delete") {
            then("validates") {
                rollback {
                    val authorId = 54321
                    val code = "code-blue"
                    resetPasswordCodeRepository.insert(code, authorId) shouldBe true
                    resetPasswordCodeRepository.getCode(authorId) shouldBe code
                    resetPasswordCodeRepository.delete(authorId)
                    resetPasswordCodeRepository.get(authorId) shouldBe null
                }
            }
        }
    }
}