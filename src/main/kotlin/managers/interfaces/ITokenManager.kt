package managers.interfaces

import dtos.authorization.TokensResult
import org.koin.core.component.KoinComponent

interface ITokenManager : KoinComponent {
    fun generateTokens(authorId: Int): TokensResult
    fun refreshAccessToken(refreshToken: String, authorId: Int): TokensResult
}