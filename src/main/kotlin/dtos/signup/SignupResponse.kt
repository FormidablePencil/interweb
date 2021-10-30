package dtos.signup

import dtos.ApiResponse
import io.ktor.http.*

class SignupResponse : ApiResponse<SignupResponseFailed>()

enum class SignupResponseFailed {
    WeakPassword, InvalidEmailFormat, EmailTaken, UsernameTaken;

    companion object {
        fun getMsg(enum: SignupResponseFailed): String {
            return when (enum) {
                WeakPassword -> "Not a strong enough password."
                InvalidEmailFormat -> "Email provided is not formatted as such."
                EmailTaken -> "Email taken."
                UsernameTaken -> "Username taken."
            }
        }

        fun getHttpCode(enum: SignupResponseFailed): HttpStatusCode {
            return when (enum) {
                WeakPassword,
                InvalidEmailFormat,
                EmailTaken,
                UsernameTaken -> HttpStatusCode.BadRequest
            }
        }
    }
}


// also combine message, status code and serialize response with one method if possible
//fun SignupResponseError.getMessage(enum: SignupResponseError): String {
//
//}
