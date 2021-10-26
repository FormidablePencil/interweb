package managers

import dto.token.TokensResult
import org.koin.core.component.KoinComponent

interface ITokenManager: KoinComponent {
    fun refreshAccessToken(refreshToken: String): Pair<String, String>
    fun generateTokens(authorId: Int, username: String): TokensResult
}