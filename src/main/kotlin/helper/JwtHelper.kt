package helper

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException

class JwtHelper {
    companion object {
        fun verifyAndGetAuthorId(token: String, secret: String): Int? {
            val verifier = JWT.require(Algorithm.HMAC256(secret)).build()
            return try {
                val result = verifier.verify(token)
                result.getClaim("authorId").asInt()
            } catch (ex: JWTVerificationException) {
                null
            }
        }
    }
}