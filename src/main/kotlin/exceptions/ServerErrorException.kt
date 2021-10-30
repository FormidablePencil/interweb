package exceptions

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import org.slf4j.LoggerFactory

class ServerErrorException : Exception {
    var errorCode: ServerError

    constructor(error: ServerError, happenedWhere: Class<*>)
            : super(ServerError.getLogMsg(error)) {
        errorCode = error
        logError((ServerError.getLogMsg(error)), happenedWhere)
    }

    constructor(error: ServerError, cause: Exception, happenedWhere: Class<*>)
            : super(ServerError.getLogMsg(error), cause) {
        errorCode = error
        logError((ServerError.getLogMsg(error)), happenedWhere)
    }

    private fun logError(msg: String, happenedWhere: Class<*>) {
        val logger = LoggerFactory.getLogger(happenedWhere)
        logger.error(msg)
    }
}

suspend fun ServerErrorException.httpRespond(call: ApplicationCall) {
    call.respond(ServerError.getHttpCode(errorCode), ServerError.getHttpMsg(errorCode))
}

enum class ServerError {
    FailedToCreateAuthor, FailedToSetNewPassword;

    companion object {
        fun getLogMsg(enum: ServerError): String {
            return when (enum) {
                FailedToCreateAuthor -> "Failed to create author."
                FailedToSetNewPassword -> "Failed to set new password."
            }
        }

        fun getHttpCode(enum: ServerError): HttpStatusCode {
            return when (enum) {
                FailedToCreateAuthor,
                FailedToSetNewPassword -> HttpStatusCode.InternalServerError
            }
        }

        fun getHttpMsg(enum: ServerError): String {
            return when (enum) {
                FailedToCreateAuthor,
                FailedToSetNewPassword -> GenericError.getMsg(GenericError.ServerError)
            }
        }
    }
}