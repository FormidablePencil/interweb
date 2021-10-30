package dtos.login

import kotlinx.serialization.Serializable
import org.valiktor.functions.hasSize
import org.valiktor.functions.isNotBlank
import org.valiktor.validate

@Serializable
data class LoginByUsernameRequest(val username: String, val password: String) {
    init {
        validate(this) {
            validate(LoginByUsernameRequest::username).isNotBlank()
            validate(LoginByUsernameRequest::password).hasSize(min = 3, max = 80)
        }
    }
}

@Serializable
data class LoginByEmailRequest(val email: String, val password: String) {
    init {
        validate(this) {
            validate(LoginByEmailRequest::email).isNotBlank()
            validate(LoginByEmailRequest::password).hasSize(min = 3, max = 80)
        }
    }
}