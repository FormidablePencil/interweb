package dtos.authorization

import dtos.ApiResponse
import dtos.IApiResponseEnum
import io.ktor.http.*

class VerifyEmailCodeResponse : ApiResponse<String, R>(R)

private typealias R = VerifyEmailCodeResponseFailed

enum class VerifyEmailCodeResponseFailed {
    DoesNotExistEmailCode, InvalidEmailCode;

    companion object : IApiResponseEnum<R> {
        override fun message(code: R): String {
            return when (code) {
                DoesNotExistEmailCode,
                InvalidEmailCode
                -> "Invalid email code."
            }
        }

        override fun statusCode(code: R): HttpStatusCode {
            return when (code) {
                DoesNotExistEmailCode,
                InvalidEmailCode
                -> HttpStatusCode.BadRequest
            }
        }
    }
}