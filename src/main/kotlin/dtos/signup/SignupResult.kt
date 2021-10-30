package dtos.signup

import dtos.DtoResult
import exceptions.GenericError

class SignupResult : DtoResult<SignupResultError>()

enum class SignupResultError {
    WeakPassword, InvalidEmailFormat, EmailTaken, UsernameTaken, ServerError;

    companion object {
        fun getMsg(enum: SignupResultError): String {
            return when (enum) {
                WeakPassword -> "Not a strong enough password."
                InvalidEmailFormat -> "Email provided is not formatted as such."
                EmailTaken -> "Email taken."
                UsernameTaken -> "Username taken."
                ServerError -> GenericError.getMsg(GenericError.ServerError)
            }
        }
    }
}


// also combine message, status code and serialize response with one method if possible
//fun SignupResultError.getMessage(enum: SignupResultError): String {
//
//}
