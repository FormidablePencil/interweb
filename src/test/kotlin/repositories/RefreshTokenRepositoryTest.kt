package repositories

import io.kotest.matchers.shouldBe
import org.koin.test.get
import repositories.interfaces.IRefreshTokenRepository
import shared.BehaviorSpecUtRepo

class RefreshTokenRepositoryTest : BehaviorSpecUtRepo({
    val refreshTokenRepository: IRefreshTokenRepository = get()
    val authorId = 0 // should fail since this doesn't exist. Testing
    val token = "refreshToken"

    given("insertToken") {
        refreshTokenRepository.insertToken(token, authorId) shouldBe true

        then("getTokenByAuthorId") {
            val tokenRes = refreshTokenRepository.getTokenByAuthorId(authorId)
                ?: throw Exception("test failed")

            tokenRes.refreshToken shouldBe token
        }

        then("deleteOldToken") {
            val f = refreshTokenRepository.deleteOldToken(authorId)
        }

        then("getTokenByAuthorId again not null") {
            refreshTokenRepository.getTokenByAuthorId(authorId)
        }

    }
})
