package managers.interfaces

import dtos.authorization.TokensResponse
import dtos.token.responseData.ITokenResponseData
import org.koin.core.component.KoinComponent

interface ITokenManager : KoinComponent {
    fun generateTokens(authorId: Int): ITokenResponseData
    fun refreshAccessToken(refreshToken: String, authorId: Int): TokensResponse
}