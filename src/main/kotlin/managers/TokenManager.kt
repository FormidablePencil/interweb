package managers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import configurations.IConfig
import repositories.IAuthorRepository
import java.util.*

class TokenManager(
    private val config: IConfig,
    private val authorRepository: IAuthorRepository,
) : ITokenManager {

    override fun authenticate(email: String, password: String) {
        // validate username and password
        authorRepository.validateCredentials(email, password)
    }

    override fun getNewAccessToken(refreshToken: String) {
        // validate refresh token
    }

    private fun generateTokens(username: String): HashMap<String, String> {
        // token table might not exist
//        tokenRepository.Authenticate(request);

        val secret = config.appConfig.property("jwt.secret").getString()
        val issuer = config.appConfig.property("jwt.issuer").getString()
        val audience = config.appConfig.property("jwt.audience").getString()
        val myRealm = config.appConfig.property("jwt.realm").getString()

        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("username", username)
            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
            .sign(Algorithm.HMAC256(secret))

        return hashMapOf("token" to token)
    }
}