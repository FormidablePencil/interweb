package managers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dto.token.TokensResult
import configurations.IAppEnv
import configurations.IConnectionToDb
import dto.succeeded
import io.ktor.util.*
import org.koin.core.component.inject
import repositories.IAuthorRepository
import repositories.ITokenRepository
import java.util.*

enum class Tokens { AccessToken, RefreshToken }

class TokenManager(
    private val authorRepository: IAuthorRepository,
    private val tokenRepository: ITokenRepository,
) : ITokenManager {
    private val appEnv: IAppEnv by inject()
    private val connectionToDb: IConnectionToDb by inject()

    override fun refreshAccessToken(refreshToken: String): Pair<String, String> {
        TODO()
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

        connectionToDb.database.useTransaction {
            tokenRepository.deleteOldTokens(username, authorId)
            tokenRepository.insertTokens(refreshToken, accessToken, authorId)
        }

        return TokensResult(refreshToken, accessToken).succeeded()
    }

    private fun generateToken(authorId: Int, username: String, kindOfToken: Tokens): HashMap<String, String> {
        val secret = appEnv.appConfig.property("jwt.secret").getString()
        val issuer = appEnv.appConfig.property("jwt.issuer").getString()
        val audience = appEnv.appConfig.property("jwt.audience").getString()
        val myRealm = appEnv.appConfig.property("jwt.realm").getString()

        val expire = when (kindOfToken) {
            Tokens.RefreshToken -> 10000
            Tokens.AccessToken -> 1200
        }

        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("authorId", authorId)
            .withClaim("username", username)
            .withExpiresAt(Date(System.currentTimeMillis() + expire))
            .sign(Algorithm.HMAC256(secret))

        return hashMapOf("token" to token)
    }
}