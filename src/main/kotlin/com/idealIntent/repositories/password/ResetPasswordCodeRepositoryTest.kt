package com.idealIntent.repositories.password

import com.idealIntent.configurations.DIHelper
import io.kotest.koin.KoinListener
import io.kotest.matchers.shouldBe
import com.idealIntent.repositories.codes.ResetPasswordCodeRepository
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