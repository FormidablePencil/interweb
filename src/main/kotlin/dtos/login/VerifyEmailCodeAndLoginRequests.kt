package dtos.login

import kotlinx.serialization.Serializable
import org.valiktor.functions.hasSize
import org.valiktor.functions.isNotBlank
import org.valiktor.validate

@Serializable
data class VerifyEmailCodeAndLoginByEmailRequest(
    val email: String,
    val password: String,
    val code: String
) {
    init {
        validate(this) {
            validate(VerifyEmailCodeAndLoginByEmailRequest::password).isNotBlank()
            validate(VerifyEmailCodeAndLoginByEmailRequest::email).isNotBlank()
            validate(VerifyEmailCodeAndLoginByEmailRequest::code).isNotBlank()
        }
    }
}

@Serializable
data class VerifyEmailCodeAndLoginByUsernameRequest(
    val username: String,
    val password: String,
    val code: String
) {
    init {
        validate(this) {
            validate(VerifyEmailCodeAndLoginByUsernameRequest::code).isNotBlank()
            validate(VerifyEmailCodeAndLoginByUsernameRequest::password).hasSize(min = 3, max = 80)
            validate(VerifyEmailCodeAndLoginByUsernameRequest::username).hasSize(min = 3, max = 80)
        }
    }
}