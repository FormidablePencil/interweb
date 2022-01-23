package serialized.auth

import dtos.login.ILoginByEmailRequest
import kotlinx.serialization.Serializable

@Serializable
class LoginByEmailRequest(override val email: String, override val password: String) : ILoginByEmailRequest {
    init {
        validate()
    }
}