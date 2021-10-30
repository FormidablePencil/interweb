package dtos.authorization

import dtos.ApiResponse

data class ResetPasswordResponse(val refreshToken: String?, val accessToken: String?) : ApiResponse<TokensResponse>()