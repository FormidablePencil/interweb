package dtos.signup

import dtos.ApiDataResponse
import dtos.IApiResponseEnum
import dtos.token.responseData.TokenResponseData
import io.ktor.http.*

class SignupResponse : ApiDataResponse<TokenResponseData, E, SignupResponse>(E)

internal typealias E = SignupResponseFailed

enum class SignupResponseFailed {
    WeakPassword,
    InvalidEmailFormat,
    EmailTaken,
    UsernameTaken;

    companion object : IApiResponseEnum<E> {
        override fun getMsg(code: E): String {
            return when (code) {
                WeakPassword -> "Not a strong enough password."
                InvalidEmailFormat -> "Email provided is not formatted as such."
                EmailTaken -> "Email taken."
                UsernameTaken -> "Username taken."
            }
        }

        override fun getStatusCode(code: E): HttpStatusCode {
            return when (code) {
                WeakPassword,
                InvalidEmailFormat,
                EmailTaken,
                UsernameTaken -> HttpStatusCode.BadRequest
            }
        }
    }
}