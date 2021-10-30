package managers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import configurations.interfaces.IAppEnv
import configurations.interfaces.IConnectionToDb
import dtos.authorization.TokensResult
import dtos.authorization.TokensResultError
import helper.failed
import helper.succeeded
import managers.interfaces.ITokenManager
import org.koin.core.component.inject
import repositories.interfaces.IRefreshTokenRepository
import java.util.*

enum class KindOfTokens { AccessToken, RefreshToken }

class TokenManager(
    private val refreshTokenRepository: IRefreshTokenRepository,
) : ITokenManager {
    private val appEnv: IAppEnv by inject()
    private val connectionToDb: IConnectionToDb by inject()

    override fun refreshAccessToken(refreshToken: String, authorId: Int): TokensResult {
        return if (!isRefreshTokenValid(refreshToken, authorId))
            TokensResult().failed(TokensResultError.InvalidRefreshToken, "Invalid refresh token")
        else generateTokens(authorId)
    }

    override fun generateTokens(authorId: Int): TokensResult {
        val refreshToken = generateToken(authorId, KindOfTokens.RefreshToken)
        val accessToken = generateToken(authorId, KindOfTokens.AccessToken)

        connectionToDb.database.useTransaction {
            refreshTokenRepository.deleteOldToken(authorId)
            refreshTokenRepository.insertToken(refreshToken, authorId)
        }

        return TokensResult(refreshToken, accessToken).succeeded()
    }

    private fun isRefreshTokenValid(refreshToken: String, authorId: Int): Boolean {
        val tokensDb = refreshTokenRepository.getTokenByAuthorId(authorId)
        return tokensDb?.refreshToken == refreshToken
    }

    private fun generateToken(authorId: Int, kindOfToken: KindOfTokens): String {
        val secret = appEnv.appConfig.property("jwt.secret").getString()
        val issuer = appEnv.appConfig.property("jwt.issuer").getString()
        val audience = appEnv.appConfig.property("jwt.audience").getString()
        val myRealm = appEnv.appConfig.property("jwt.realm").getString()

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
//        return hashMapOf("token" to token)
    }
}