package dtos.signup

import dtos.*

data class SignupResult(var authorId: Int? = null) : DtoResult<SignupResultError>()

enum class SignupResultError {
    ServerError, WeakPassword, InvalidEmailFormat
}