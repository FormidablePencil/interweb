package serialized.auth

import dtos.responseData.ITokenResponseData
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponseData(override val accessToken: String, override val refreshToken: String) : ITokenResponseData