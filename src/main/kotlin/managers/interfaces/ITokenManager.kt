package managers.interfaces

import dtos.authorization.TokensResponse
import org.koin.core.component.KoinComponent

interface ITokenManager : KoinComponent {
    fun generateTokens(authorId: Int): TokensResponse
    fun refreshAccessToken(refreshToken: String, authorId: Int): TokensResponse
}