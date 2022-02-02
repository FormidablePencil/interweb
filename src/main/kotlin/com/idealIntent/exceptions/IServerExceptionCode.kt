package com.idealIntent.exceptions

import io.ktor.http.*

/**
 * Enum codes for logging and responding to client in a user-friendly manner.
 */
interface IServerExceptionCode<EnumCode: Enum<EnumCode>> {
    /**
     * Contextual server error response for when you'd like the client to know what when wrong with the server.
     */
    fun contextualServerErrorResponse(code: EnumCode) = "$genericServerError ${code.name}"

    /**
     * Log the attempt to log a client error as a server error.
     */
    fun logAttemptToLogClientAsServerError(code: EnumCode) = "Tried to log a client error as a server error: #${code.name}"

    val genericServerError: String
        get() = GenericError.getMsg(GenericError.ServerError)

    /**
     * This method is reserved for logging purposes. Get message of code for logging.
     *
     * @return Log error with message corresponding to enum.
     */
    fun getLogMsg(code: EnumCode): String

    /**
     * Get client-friendly message of code.
     *
     * Respond to client with these messages when exception with enum bubbles up to [routeRespond][com.idealIntent.routes.routeRespond].
     * @return message corresponding to [enum].
     */
    fun getClientMsg(code: EnumCode): String

    /**
     * Get http status code of enum code.
     *
     * @param enum GenericError enum
     * @return Http status code corresponding to [enum].
     */
    fun getHttpCode(code: EnumCode): HttpStatusCode
}
