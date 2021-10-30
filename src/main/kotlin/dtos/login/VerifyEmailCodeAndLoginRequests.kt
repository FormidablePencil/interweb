package dtos.login

import kotlinx.serialization.Serializable
import org.valiktor.functions.hasSize
import org.valiktor.functions.isNotBlank
import org.valiktor.validate

@Serializable
data class VerifyEmailCodeThenLoginByEmailRequest(
    val email: String,
    val password: String,
    val code: String
) {
    init {
        validate(this) {
            validate(VerifyEmailCodeThenLoginByEmailRequest::password).isNotBlank()
            validate(VerifyEmailCodeThenLoginByEmailRequest::email).isNotBlank()
            validate(VerifyEmailCodeThenLoginByEmailRequest::code).isNotBlank()
        }
    }
}

@Serializable
data class VerifyEmailCodeThenLoginByUsernameRequest(
    val username: String,
    val password: String,
    val code: String
) {
    init {
        validate(this) {
            validate(VerifyEmailCodeThenLoginByUsernameRequest::code).isNotBlank()
            validate(VerifyEmailCodeThenLoginByUsernameRequest::password).hasSize(min = 3, max = 80)
            validate(VerifyEmailCodeThenLoginByUsernameRequest::username).hasSize(min = 3, max = 80)
        }
    }
}