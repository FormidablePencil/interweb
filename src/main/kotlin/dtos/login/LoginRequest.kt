package dtos.login
import kotlinx.serialization.Serializable
import org.valiktor.functions.hasSize
import org.valiktor.functions.isNotBlank
import org.valiktor.validate

//fun LoginApiRequest.validate(): Boolean = username.length > 5

@Serializable
data class LoginRequest(var username: String, var password: String) {
    init {
        validate(this) {
            validate(LoginRequest::username).isNotBlank()
            validate(LoginRequest::password).hasSize(min = 3, max = 80)
        }
    }
}