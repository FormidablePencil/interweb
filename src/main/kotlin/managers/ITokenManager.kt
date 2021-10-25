package managers

import dto.token.TokensResult

interface ITokenManager {
    fun refreshAccessToken(refreshToken: String): Pair<String, String>
    fun generateTokens(authorId: Int, username: String): TokensResult
}