package dtos.authorization

import dtos.ApiResponse

data class RequestPasswordResetResponse(val maskedEmail: String? = null) : ApiResponse<RequestPasswordResetResponseFailed>()

enum class RequestPasswordResetResponseFailed {
    AccountNotFoundByGivenEmail, AccountNotFoundByGivenUsername, NeitherUsernameNorEmailProvided
}
