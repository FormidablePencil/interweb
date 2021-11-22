package managers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import configurations.interfaces.IAppEnv
import configurations.interfaces.IConnectionToDb
import dtos.authorization.TokensResponse
import dtos.authorization.TokensResponseFailed
import dtos.failed
import dtos.succeeded
import io.ktor.http.*
import managers.interfaces.ITokenManager
import org.koin.core.component.inject
import repositories.interfaces.IRefreshTokenRepository
import serialized.TokenResponseData
import java.util.*

enum class KindOfTokens { AccessToken, RefreshToken }

class TokenManager(
    private val refreshTokenRepository: IRefreshTokenRepository,
) : ITokenManager {
    private val appEnv: IAppEnv by inject()
    private val connectionToDb: IConnectionToDb by inject()

    override fun refreshAccessToken(refreshToken: String, authorId: Int): TokensResponse {
        return if (isRefreshTokenValid(refreshToken, authorId)) {
            val newTokens = generateTokens(authorId)
            TokensResponse().succeeded(HttpStatusCode.Created, newTokens)
        } else
            TokensResponse().failed(TokensResponseFailed.InvalidRefreshToken)
    }

    override fun generateTokens(authorId: Int): TokenResponseData {
        val refreshToken = generateToken(authorId, KindOfTokens.RefreshToken)
        val accessToken = generateToken(authorId, KindOfTokens.AccessToken)

        connectionToDb.database.useTransaction {
            refreshTokenRepository.deleteOldToken(authorId)
            refreshTokenRepository.insertToken(refreshToken, authorId)
        }

        return TokenResponseData(refreshToken, accessToken)
    }

    private fun isRefreshTokenValid(refreshToken: String, authorId: Int): Boolean {
        val tokensDb = refreshTokenRepository.getTokenByAuthorId(authorId)
        return tokensDb?.refreshToken == refreshToken
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