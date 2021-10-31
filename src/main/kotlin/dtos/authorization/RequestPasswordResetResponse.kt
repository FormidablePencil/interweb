package dtos.authorization

import dtos.ApiResponse
import dtos.IApiResponseEnum
import io.ktor.http.*
import responseData.PasswordResetResponseData

// test a non instantiated object. Is this by reference or not?
object RequestPasswordResetResponse : ApiResponse<PasswordResetResponseData, V>(V)

internal typealias V = RequestPasswordResetResponseFailed

enum class RequestPasswordResetResponseFailed {
    AccountNotFoundByGivenEmail, AccountNotFoundByGivenUsername, NeitherUsernameNorEmailProvided;

    companion object : IApiResponseEnum<V> {
        override fun message(code: V): String {
            return when (code) {
                AccountNotFoundByGivenEmail,
                AccountNotFoundByGivenUsername,
                NeitherUsernameNorEmailProvided -> "Account not found by given email."
            }
        }

        override fun statusCode(code: V): HttpStatusCode {
            return when (code) {
                AccountNotFoundByGivenEmail,
                AccountNotFoundByGivenUsername,
                NeitherUsernameNorEmailProvided -> HttpStatusCode.BadRequest
            }
        }
    }
}