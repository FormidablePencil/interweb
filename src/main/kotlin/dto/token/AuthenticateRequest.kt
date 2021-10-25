package dto.token

import kotlinx.serialization.Serializable

data class AuthenticateRequest(val email: String, val password: String)