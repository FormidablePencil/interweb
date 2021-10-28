package managers.interfaces

import dtos.authorization.TokensResult
import org.koin.core.component.KoinComponent

interface ITokenManager : KoinComponent {
    fun genTokensOnSignup(authorId: Int): Pair<String?, String?>
    fun genTokensOnResetPassword(authorId: Int): TokensResult
    fun refreshAccessToken(refreshToken: String, authorId: Int): TokensResult
}