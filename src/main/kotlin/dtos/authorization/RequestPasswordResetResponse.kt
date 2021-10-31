package dtos.authorization

import dtos.ApiDataResponse
import dtos.IApiResponseEnum
import io.ktor.http.*
import responseData.PasswordResetResponseData

// test a non instantiated object. Is this by reference or not?
object RequestPasswordResetResponse : ApiDataResponse<PasswordResetResponseData, V, RequestPasswordResetResponse>(V)

internal typealias V = RequestPasswordResetResponseFailed

enum class RequestPasswordResetResponseFailed {
    AccountNotFoundByGivenEmail, AccountNotFoundByGivenUsername, NeitherUsernameNorEmailProvided;

    companion object : IApiResponseEnum<V> {
        override fun getMsg(code: V): String {
            return when (code) {
                AccountNotFoundByGivenEmail,
                AccountNotFoundByGivenUsername,
                NeitherUsernameNorEmailProvided -> "Account not found by given email."
            }
        }

        override fun getStatusCode(code: V): HttpStatusCode {
            return when (code) {
                AccountNotFoundByGivenEmail,
                AccountNotFoundByGivenUsername,
                NeitherUsernameNorEmailProvided -> HttpStatusCode.BadRequest
            }
        }
    }
}