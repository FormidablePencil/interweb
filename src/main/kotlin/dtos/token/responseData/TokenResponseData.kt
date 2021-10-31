package dtos.token.responseData

@kotlinx.serialization.Serializable
data class TokenResponseData(val refreshToken: String, val accessToken: String)
