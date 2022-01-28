package com.idealIntent.repositories

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.koin.test.inject
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.rollback

class RefreshTokenRepositoryTest : BehaviorSpecUtRepo({
    val refreshTokenRepository: RefreshTokenRepository by inject()
    val authorId = 0 // todo - should fail since this doesn't exist. Testing
    val token = "refreshToken"

    // todo -
    //  authorProfileRelatedRepository.createNewAuthor() must happen first
    xgiven("insert, delete, get") {
        then("should work") {
            rollback {
                refreshTokenRepository.insert(token, authorId) shouldBe true

                val getRes = refreshTokenRepository.get(authorId)

                getRes shouldNotBe null
                getRes?.refreshToken shouldBe token

                refreshTokenRepository.delete(authorId) shouldBe true

                refreshTokenRepository.get(authorId) shouldBe null
            }
        }
    }
})
