package managers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import configurations.AppEnv
import configurations.interfaces.IConnectionToDb
import dtos.authorization.TokensResponse
import dtos.authorization.TokensResponseFailed
import dtos.failed
import dtos.succeeded
import io.ktor.http.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repositories.RefreshTokenRepository
import serialized.TokenResponseData
import java.util.*

enum class KindOfTokens { AccessToken, RefreshToken }

class TokenManager(
    private val refreshTokenRepository: RefreshTokenRepository,
) : KoinComponent {
    private val appEnv: AppEnv by inject()
    private val connectionToDb: IConnectionToDb by inject()

    fun refreshAccessToken(refreshToken: String, authorId: Int): TokensResponse {
        return if (isRefreshTokenValid(refreshToken, authorId)) {
            val newTokens = generateTokens(authorId)
            TokensResponse().succeeded(HttpStatusCode.Created, newTokens)
        } else
            TokensResponse().failed(TokensResponseFailed.InvalidRefreshToken)
    }

    fun generateTokens(authorId: Int): TokenResponseData {
        val refreshToken = generateToken(authorId, KindOfTokens.RefreshToken)
        val accessToken = generateToken(authorId, KindOfTokens.AccessToken)

        connectionToDb.database.useTransaction {
            refreshTokenRepository.deleteOldToken(authorId)
            refreshTokenRepository.insertToken(refreshToken, authorId)
        }

        return TokenResponseData(refreshToken = refreshToken, accessToken = accessToken)
    }

    private fun isRefreshTokenValid(refreshToken: String, authorId: Int): Boolean {
        return refreshTokenRepository.getTokenByAuthorId(authorId)?.refreshToken == refreshToken
    }

    private fun generateToken(authorId: Int, kindOfToken: KindOfTokens): String {
        val secret = appEnv.appConfig.property("jwt.secret").getString()
        val issuer = appEnv.appConfig.property("jwt.issuer").getString()
        val audience = appEnv.appConfig.property("jwt.audience").getString()
        val myRealm = appEnv.appConfig.property("jwt.realm").getString() // todo - what is this?

        val expire = when (kindOfToken) {
            KindOfTokens.RefreshToken -> 10000
            KindOfTokens.AccessToken -> 1200
        }

        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("authorId", authorId)
            .withExpiresAt(Date(System.currentTimeMillis() + expire))
            .sign(Algorithm.HMAC256(secret))
    }
}