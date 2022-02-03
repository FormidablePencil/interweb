package com.idealIntent.exceptions

import io.ktor.http.*

/**
 * Enum codes for logging and responding to client in a user-friendly manner.
 *
 * For the sake of brevity, we'll use the word error in place of enum.
 */
interface IServerExceptionCode<EnumCode : Enum<EnumCode>> {

    /**
     * Contextual server error response for when you'd like the client to know what when wrong with the server.
     */
    fun contextualServerErrorResponse(code: EnumCode)
    = "$genericServerError ${getLogMsg(code)}"

    /**
     * Log the attempt to log a client error as a server error.
     */
    fun logAttemptToLogClientAsServerError(code: EnumCode) =
        "Tried to log a client error as a internal error: #${code.name}"

    val genericServerError: String
        get() = GenericError.getMsg(GenericError.ServerError)

    /**
     * Reserved for logging. Gets message of code for logging.
     *
     *@return
     * First variant - When on errors that are meant to be logged return the corresponding messages.
     *
     * Second variant - When on errors that where meant to be responded the client with message. Return a message that
     * will be logged. And log the mistake.
     */
    fun getLogMsg(code: EnumCode): String

    /**
     * Reserved for client message responses. Gets client-friendly message of error code and returns to the
     * client with it.
     *
     * Response with an error message happens in either 2 ways. SpaceResponseFailed custom exception such as [CompositionException] is thrown that propagates to
     * [route controller handler][com.idealIntent.routes.routeRespond] and returns a message response corresponding to the [error code][code].
     * The second way is passing the [error code][code] to the [dtos.auth.RequestPasswordResetResponse] as failed, propagated through functions
     * (not throwing propagation to be clear) and returns it at [route controller handler][com.idealIntent.routes.routeRespond].
     *
     * @return
     * First variant - Errors made by client request, return a corresponding message to corresponding error.
     *
     * Second variant - Errors that are suspected to be a bug of some edge, returns a generic internal error message.
     *
     * Third variant - Errors that are meant to logged and returned to the client will return
     * the corresponding message with a generic internal server error message prepended.
     */
    fun getClientMsg(code: EnumCode): String

    /**
     * Reserved for client http responses. Gets http status code of error.
     */
    fun getHttpCode(code: EnumCode): HttpStatusCode
}
