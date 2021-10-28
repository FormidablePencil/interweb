package dtos.authorization

import dtos.DtoResult

data class RequestPasswordResetResult(val maskedEmail: String? = null) : DtoResult<RequestPasswordResetResultError>()

enum class RequestPasswordResetResultError {
    AccountNotFoundByGivenEmail, AccountNotFoundByGivenUsername, NeitherUsernameNorEmailProvided
}
