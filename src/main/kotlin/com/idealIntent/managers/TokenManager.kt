package com.idealIntent.managers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.idealIntent.configurations.AppEnv
import dtos.authorization.TokensResponse
import dtos.authorization.TokensResponseFailed
import dtos.failed
import dtos.succeeded
import io.ktor.http.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.idealIntent.repositories.RefreshTokenRepository
import com.idealIntent.serialized.auth.TokenResponseData
import java.util.*

enum class KindOfTokens { AccessToken, RefreshToken }

class TokenManager(
    private val refreshTokenRepository: RefreshTokenRepository,
) : KoinComponent {
    private val appEnv: AppEnv by inject()

    fun refreshAccessToken(refreshToken: String, authorId: Int): TokensResponse {
        return if (isRefreshTokenValid(refreshToken, authorId)) {
            val newTokens = generateAuthTokens(authorId)
            TokensResponse().succeeded(HttpStatusCode.Created, newTokens)
        } else
            TokensResponse().failed(TokensResponseFailed.InvalidRefreshToken)
    }

    fun generateAuthTokens(authorId: Int): TokenResponseData {
        val refreshToken = generateToken(authorId, KindOfTokens.RefreshToken)
        val accessToken = generateToken(authorId, KindOfTokens.AccessToken)

        appEnv.database.useTransaction {
            refreshTokenRepository.delete(authorId)
            refreshTokenRepository.insert(refreshToken, authorId)
        }

        return TokenResponseData(refreshToken = refreshToken, accessToken = accessToken)
    }

    private fun isRefreshTokenValid(refreshToken: String, authorId: Int): Boolean {
        return refreshTokenRepository.get(authorId)?.refreshToken == refreshToken
    }

    private fun generateToken(authorId: Int, kindOfToken: KindOfTokens): String {
        val secret = appEnv.getConfig("jwt.secret")
        val issuer = appEnv.getConfig("jwt.issuer")
        val audience = appEnv.getConfig("jwt.audience")
        val myRealm = appEnv.getConfig("jwt.realm") // todo - what is this?

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