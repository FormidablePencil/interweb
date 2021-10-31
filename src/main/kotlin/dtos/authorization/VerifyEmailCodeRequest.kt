package dtos.authorization

import org.valiktor.functions.isNotBlank
import org.valiktor.validate

data class VerifyEmailCodeRequest(val code: String) {
    init {
        validate(this) {
            validate(VerifyEmailCodeRequest::code).isNotBlank()
        }
    }
}