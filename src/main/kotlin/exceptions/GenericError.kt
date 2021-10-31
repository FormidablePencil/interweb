package exceptions

import io.ktor.http.*

enum class GenericError {
    ServerError;

    companion object {
        fun getMsg(enum: GenericError): String {
            return when (enum) {
                ServerError -> "Server code."
            }
        }

        fun getHttpCode(enum: GenericError): HttpStatusCode {
            return when (enum) {
                ServerError -> HttpStatusCode.InternalServerError
            }
        }
    }
}