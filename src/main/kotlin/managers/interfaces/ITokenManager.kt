package managers.interfaces

import dtos.authorization.TokensResponse
import dtos.token.responseData.TokenResponseData
import org.koin.core.component.KoinComponent

interface ITokenManager : KoinComponent {
    fun generateTokens(authorId: Int): TokenResponseData
    fun refreshAccessToken(refreshToken: String, authorId: Int): TokenResponseData
}