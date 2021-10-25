package managers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import configurations.IConfig
import dto.token.TokensResult
import repositories.IAuthorRepository
import repositories.ITokenRepository
import java.util.*

enum class Tokens { AccessToken, RefreshToken }

class TokenManager(
    private val config: IConfig,
    private val authorRepository: IAuthorRepository,
    private val tokenRepository: ITokenRepository,
) : ITokenManager {

    override fun refreshAccessToken(refreshToken: String): Pair<String, String> {
        throw NotImplementedError()
        // validate refresh token
        validateRefreshToken(refreshToken)

//        val result = generateToken()

        return Pair<String, String>("new AccessToken", "new RefreshToken")
    }

    private fun validateRefreshToken(refreshToken: String) {
        throw NotImplementedError()
        // get refresh token from db and compare the two. Don't try
    }

    override fun generateTokens(authorId: Int, username: String): TokensResult {
        val refreshToken = generateToken(authorId, username, Tokens.RefreshToken)
        val accessToken = generateToken(authorId, username, Tokens.AccessToken)

        // save both of them in the db under authorId
        tokenRepository.insertTokens(refreshToken, accessToken, authorId)

        return TokensResult(refreshToken, accessToken)
    }

    private fun generateToken(authorId: Int, username: String, kindOfToken: Tokens): HashMap<String, String> {
        val secret = config.appConfig.property("jwt.secret").getString()
        val issuer = config.appConfig.property("jwt.issuer").getString()
        val audience = config.appConfig.property("jwt.audience").getString()
        val myRealm = config.appConfig.property("jwt.realm").getString()

        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("authorId", authorId)
            .withClaim("username", username)
            .withExpiresAt(Date(System.currentTimeMillis() + 1200))
            .sign(Algorithm.HMAC256(secret))

        return hashMapOf("token" to token)
    }
}