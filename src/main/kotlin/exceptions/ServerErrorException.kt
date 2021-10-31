package exceptions

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import org.slf4j.LoggerFactory

class ServerErrorException : Exception {
    var errorCode: ServerFailed

    constructor(error: ServerFailed, happenedWhere: Class<*>)
            : super(ServerFailed.getLogMsg(error)) {
        errorCode = error
        logError((ServerFailed.getLogMsg(error)), happenedWhere)
    }

    constructor(error: ServerFailed, cause: Exception, happenedWhere: Class<*>)
            : super(ServerFailed.getLogMsg(error), cause) {
        errorCode = error
        logError((ServerFailed.getLogMsg(error)), happenedWhere)
    }

    private fun logError(msg: String, happenedWhere: Class<*>) {
        val logger = LoggerFactory.getLogger(happenedWhere)
        logger.error(msg)
    }
}

suspend fun ServerErrorException.httpRespond(call: ApplicationCall) {
    call.respond(ServerFailed.getHttpCode(errorCode), ServerFailed.getHttpMsg(errorCode))
}

enum class ServerFailed {
    FailedToCreateAuthor, DoesNotExistEmailCode, FailedToRetrievePassword, AttemptedToAccessDataWhenNot;


    companion object {
        fun getLogMsg(enum: ServerFailed): String {
            return when (enum) {
                FailedToCreateAuthor -> "Failed to create author."
//                FailedToSetNewPassword -> "Failed to set new password."
                DoesNotExistEmailCode -> "Email verification code was supposed to exist in our records."
                FailedToRetrievePassword -> "Failed to retrieve password."
            }
        }

        fun getHttpCode(enum: ServerFailed): HttpStatusCode {
            return when (enum) {
                else -> HttpStatusCode.InternalServerError
            }
        }

        fun getHttpMsg(enum: ServerFailed): String {
            return when (enum) {
                else -> GenericError.getMsg(GenericError.ServerError)
            }
        }
    }
}