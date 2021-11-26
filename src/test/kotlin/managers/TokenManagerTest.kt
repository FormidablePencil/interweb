package managers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.typesafe.config.ConfigFactory
import configurations.AppEnv
import dtos.authorization.TokensResponseFailed
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.ktor.config.*
import io.ktor.http.*
import io.mockk.*
import models.authorization.Token
import repositories.RefreshTokenRepository
import serialized.TokenResponseData
import shared.appEnvMockHelper

class TokenManagerTest : BehaviorSpec({
    val refreshTokenRepository: RefreshTokenRepository = mockk()
    val tokenDb: Token = mockk()
    val authorId = 321
    val tokens = TokenResponseData("access token", "refresh token")
    val appEnv = mockk<AppEnv>()
    val configs = HoconApplicationConfig(ConfigFactory.load())

    val tokenManager = spyk(TokenManager(refreshTokenRepository))

    beforeEach {
        clearMocks(refreshTokenRepository, tokenDb)

        appEnvMockHelper(appEnv, tokenManager)

        val capturedPath = slot<String>()
        every { appEnv.getConfig(capture(capturedPath)) } answers {
            configs.property(capturedPath.captured).getString()
        }

        every { refreshTokenRepository.deleteOldToken(authorId) } returns true
        every { refreshTokenRepository.insertToken(any(), authorId) } returns true

        every { tokenDb.refreshToken } returns tokens.refreshToken
        every { refreshTokenRepository.getTokenByAuthorId(authorId) } returns tokenDb
    }

    given("refreshAccessToken") {
        then("invalid refresh token") {
            val res = tokenManager.refreshAccessToken("invalid token", authorId)

            res.statusCode() shouldBe HttpStatusCode.BadRequest
            res.message() shouldBe TokensResponseFailed.getMsg(TokensResponseFailed.InvalidRefreshToken)
        }
        then("valid refresh token") {
            val res = tokenManager.refreshAccessToken(tokens.refreshToken, authorId)

            res.statusCode() shouldBe HttpStatusCode.Created
            res.data!!.refreshToken.length shouldBeGreaterThan 0
            res.data!!.accessToken.length shouldBeGreaterThan 0
        }
    }

    given("generateTokens") {
        then("valid everything") {
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
    }
})
