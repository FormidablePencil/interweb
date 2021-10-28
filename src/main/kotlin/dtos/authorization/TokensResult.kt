package dtos.authorization

import dtos.DtoResult

data class TokensResult(
    val refreshToken: String? = null, val accessToken: String? = null
) : DtoResult<TokensResultError>()

enum class TokensResultError {
    InvalidRefreshToken
}
