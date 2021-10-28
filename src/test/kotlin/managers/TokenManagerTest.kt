package managers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.mockk.every
import io.mockk.mockk
import repositories.interfaces.ITokenRepository
import shared.BehaviorSpecUT
import java.util.*

// take a look at viewing code coverage

class TokenManagerTest : BehaviorSpecUT({
    var tokenRepository: ITokenRepository = mockk()
    val authorId = 321

    every { tokenRepository.deleteOldTokens(authorId) } returns 1
    every { tokenRepository.insertTokens(any(), any(), authorId) } returns 1

    var tokenManager = TokenManager(tokenRepository)

    given("refreshAccessToken") {
        // private method nested: validateRefreshToken

        // check that the provided refresh token is being validated
        When("provided valid refreshToken and authorId") {
            val refreshToken = JWT.create()
                .withAudience("audience")
                .withIssuer("issuer")
                .withClaim("authorId", authorId)
                .withExpiresAt(Date(System.currentTimeMillis() + 2000))
                .sign(Algorithm.HMAC256("secret"))

            val result = tokenManager.refreshAccessToken(refreshToken, authorId)

            Then("it returns tokens") {
                // this may not work if it's just going to ignore it and move on to the next line if fails
                // however, the framework's shouldBeGreaterThan should have us covered
                // test a failed state
                result.accessToken?.length?.shouldBeGreaterThan(1)

//                TODO() // decode tokens and check that expiration, authorId and username is correct
            }
        }
        When("provided tempered refreshToken but valid authorId") {

        }
        When("provided authorId that doesn't exist") {

        }
    }
})