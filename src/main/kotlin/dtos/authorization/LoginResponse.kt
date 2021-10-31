package dtos.authorization

import dtos.ApiDataResponse
import dtos.IApiResponseEnum
import dtos.token.responseData.TokenResponseData
import io.ktor.http.*

class LoginResponse : ApiDataResponse<TokenResponseData, E, LoginResponse>(E)

internal typealias E = LoginResponseFailed

enum class LoginResponseFailed {
    InvalidEmail,
    InvalidUsername,
    InvalidPassword;

    companion object : IApiResponseEnum<E> {
        override fun getMsg(code: E): String {

            return when (code) {
                InvalidEmail,
                InvalidUsername,
                InvalidPassword -> "Invalid credentials."
            }
        }

        override fun getStatusCode(code: E): HttpStatusCode {
            return when (code) {
                InvalidEmail,
                InvalidUsername,
                InvalidPassword -> HttpStatusCode.BadRequest
            }
        }
    }
}