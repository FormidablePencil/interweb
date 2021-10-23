package domainServices

import dto.Token.AuthenticateRequest
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import configurations.Config
import configurations.IConfig
import repositories.TokenRepository
import java.util.*

class TokenDomainService(
    private val config: IConfig
) {
    fun Authenticate(request: AuthenticateRequest): String {
        // token table might not exist
//        tokenRepository.Authenticate(request);

        val secret = config.appConfig.property("jwt.secret").getString()
        val issuer = config.appConfig.property("jwt.issuer").getString()
        val audience = config.appConfig.property("jwt.audience").getString()
        val myRealm = config.appConfig.property("jwt.realm").getString()

        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("username", request.username)
            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
            .sign(Algorithm.HMAC256(secret))

        return token
    }
}