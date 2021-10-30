package dtos.authorization

import dtos.ApiResponse

data class TokensResponse(
    val refreshToken: String? = null, val accessToken: String? = null
) : ApiResponse<TokensResponseFailed>()

enum class TokensResponseFailed {
    InvalidRefreshToken
}
