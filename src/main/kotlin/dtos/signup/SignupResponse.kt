package dtos.signup

import dtos.ApiResponse
import dtos.IApiResponseEnum
import dtos.token.responseData.TokenResponseData
import io.ktor.http.*

class SignupResponse : ApiResponse<TokenResponseData, E>(E)

internal typealias E = SignupResponseFailed

enum class SignupResponseFailed {
    WeakPassword,
    InvalidEmailFormat,
    EmailTaken,
    UsernameTaken;

    companion object : IApiResponseEnum<E> {
        override fun message(code: E): String {
            return when (code) {
                WeakPassword -> "Not a strong enough password."
                InvalidEmailFormat -> "Email provided is not formatted as such."
                EmailTaken -> "Email taken."
                UsernameTaken -> "Username taken."
            }
        }

        override fun statusCode(code: E): HttpStatusCode {
            return when (code) {
                WeakPassword,
                InvalidEmailFormat,
                EmailTaken,
                UsernameTaken -> HttpStatusCode.BadRequest
            }
        }
    }
}