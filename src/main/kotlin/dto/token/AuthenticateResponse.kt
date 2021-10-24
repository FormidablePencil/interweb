package dto.token

import kotlinx.serialization.Serializable

data class AuthenticateResponse(val username: String, val password: String)