package dto.login
import kotlinx.serialization.*
import org.valiktor.functions.hasSize
import org.valiktor.functions.isEmail
import org.valiktor.functions.isNotBlank
import org.valiktor.validate

fun Login.validate(): Boolean = username.length > 5

@Serializable
data class Login(var username: String, var password: String) {
    init {
        validate(this) {
            validate(Login::username).isNotBlank()
            validate(Login::password).hasSize(min = 3, max = 80)
        }
    }
}