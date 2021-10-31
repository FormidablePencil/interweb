package dtos.authorization

import dtos.ApiResponse
import dtos.IApiResponseEnum
import io.ktor.http.*

class VerifyEmailCodeResponse : ApiResponse<R, VerifyEmailCodeResponse>(R)

private typealias R = VerifyEmailCodeResponseFailed

enum class VerifyEmailCodeResponseFailed {
    DoesNotExistEmailCode, InvalidEmailCode;

    companion object : IApiResponseEnum<R> {
        override fun getMsg(code: R): String {
            return when (code) {
                DoesNotExistEmailCode,
                InvalidEmailCode
                -> "Invalid email code."
            }
        }

        override fun getStatusCode(code: R): HttpStatusCode {
            return when (code) {
                DoesNotExistEmailCode,
                InvalidEmailCode
                -> HttpStatusCode.BadRequest
            }
        }
    }
}