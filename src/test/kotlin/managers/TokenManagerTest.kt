package managers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.typesafe.config.ConfigFactory
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.ktor.config.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import repositories.interfaces.IRefreshTokenRepository
import shared.testUtils.BehaviorSpecUT
import java.util.*

// take a look at viewing getStatusCode coverage

class TokenManagerTest : BehaviorSpecUT({
    val refreshTokenRepository: IRefreshTokenRepository = mockk()
    val authorId = 321

    val tokenManager = TokenManager(refreshTokenRepository)

    xgiven("refreshAccessToken") {

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

//                TODO() // decode tokens and check that expiration, authorId and email is correct
            }
        }
        When("provided tempered refreshToken but valid authorId") {

        }
        When("provided authorId that doesn't exist") {

        }
    }

    given("generateTokens") {
        every { refreshTokenRepository.deleteOldToken(authorId) } returns true
        every { refreshTokenRepository.insertToken(any(), authorId) } returns true

        val result = tokenManager.generateTokens(authorId)

        verifySequence {
            refreshTokenRepository.deleteOldToken(authorId)
            refreshTokenRepository.insertToken(any(), authorId)
        }

        result.refreshToken.length shouldBeGreaterThan 0
        result.accessToken.length shouldBeGreaterThan 0

        try {
            val appConfig: ApplicationConfig = HoconApplicationConfig(ConfigFactory.load("application.conf"))
            val secret = appConfig.property("jwt.secret").getString()
            val issuer = appConfig.property("jwt.issuer").getString()
            val audience = appConfig.property("jwt.audience").getString()

            val verifier = JWT.require(Algorithm.HMAC256(secret))
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim("authorId", authorId)
                .build()

            verifier.verify(result.refreshToken)
            verifier.verify(result.accessToken)
        } catch (ex: JWTVerificationException) {
            throw ex
        }
    }
})
