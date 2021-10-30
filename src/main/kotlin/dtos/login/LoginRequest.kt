package dtos.login

import kotlinx.serialization.Serializable
import org.valiktor.functions.hasSize
import org.valiktor.functions.isNotBlank
import org.valiktor.validate

@Serializable
data class LoginRequest(val username: String, val password: String) {
    init {
        validate(this) {
            validate(LoginRequest::username).isNotBlank()
            validate(LoginRequest::password).hasSize(min = 3, max = 80)
        }
    }
}