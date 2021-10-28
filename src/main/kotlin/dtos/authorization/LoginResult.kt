package dtos.authorization

import dtos.DtoResult

data class LoginResult(val authorId: Int? = null, val tokens: TokensResult? = null) : DtoResult<LoginResultError>()

enum class LoginResultError {
    InvalidEmail, InvalidPassword
}
