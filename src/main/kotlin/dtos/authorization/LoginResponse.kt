package dtos.authorization

import dtos.ApiResponse
import dtos.IApiResponseEnum
import dtos.token.responseData.TokenResponseData
import io.ktor.http.*

class LoginResponse : ApiResponse<TokenResponseData, E>(E)

internal typealias E = LoginResponseFailed

enum class LoginResponseFailed {
    InvalidEmail,
    InvalidUsername,
    InvalidPassword;

    companion object : IApiResponseEnum<E> {
        override fun message(code: E): String {

            return when (code) {
                InvalidEmail,
                InvalidUsername,
                InvalidPassword -> "Invalid credentials."
            }
        }

        override fun statusCode(code: E): HttpStatusCode {
            return when (code) {
                InvalidEmail,
                InvalidUsername,
                InvalidPassword -> HttpStatusCode.BadRequest
            }
        }
    }
}