package dtos.signup

import dtos.DtoResult

data class SignupResult(var authorId: Int? = null) : DtoResult<SignupResultError>()

enum class SignupResultError {
    ServerError, WeakPassword, InvalidEmailFormat
}