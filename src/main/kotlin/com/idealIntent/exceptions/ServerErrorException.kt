package com.idealIntent.exceptions

import org.slf4j.LoggerFactory

// todo - test if you can move this::class to exception itself.
/**
 * Logger and custom exception.
 *
 * In Julia.main project an exception may propagate to the [routing level and responds][com.idealIntent.routes.routeRespond]
 * to the client with a message.
 *
 * Is inherited by error logging exceptions such as [CompositionExceptionReport]. When thrown, it will log the error and
 * propagate the exception. The exception not be caught until it reaches [routeRespond][com.idealIntent.routes.routeRespond]
 * which responds to the client with [message].
 *
 * @property message Message is always a client-friendly response.
 * @property Throwable.message Message is always a client-friendly response.
 * @property clientMsg A client-friendly response message. E.g. [CompositionCodeReport.getClientMsg].
 * @constructor logs and throws exception.
 */
open class ServerErrorException : Exception {
    val clientMsg: String
    override val message: Nothing? = null

    /**
     * @param logMsg Message to log.
     * @param clientMsg Client-friendly message that gets assigned to message of [throwable message][Throwable.message].
     * @param happenedWhere this::class.java. Used to trace log.
     */
    constructor(logMsg: String, clientMsg: String, happenedWhere: Class<*>) {
        this.clientMsg = clientMsg
        logError(logMsg, happenedWhere)
    }

    /**
     * @param logMsg Message to log.
     * @param clientMsg Client-friendly message that gets assigned to message of [throwable message][Throwable.message].
     * @param happenedWhere this::class.java. Used to trace log.
     * @param cause Propagate exception thrown.
     */
    constructor(logMsg: String, clientMsg: String, happenedWhere: Class<*>, cause: Exception) {
        this.clientMsg = clientMsg
        logError(logMsg, happenedWhere)
    }

    /**
     * Log and throw [clientMsg].
     *
     * @param logMsg Message to log.
     * @param clientMsg Client-friendly message that gets assigned to message of [throwable message][Throwable.message].
     * @param nameLocation When exception is thrown in a function rather than a class where "this::class.java" is available.
     *
     */
    constructor(logMsg: String, clientMsg: String, nameLocation: String) {
        this.clientMsg = clientMsg
        logErrorName(logMsg, nameLocation)
    }

    /**
     * Log and throw [clientMsg] with [exception cause][cause] to propagate.
     *
     * @param logMsg Message to log.
     * @param clientMsg Client-friendly message that gets assigned to message of [throwable message][Throwable.message].
     * @param nameLocation When exception is thrown in a function rather than a class where "this::class.java" is available.
     * @param cause Propagate exception thrown.
     */
    constructor(logMsg: String, clientMsg: String, nameLocation: String, cause: Exception) {
        this.clientMsg = clientMsg
        logErrorName(logMsg, nameLocation)
    }

    /**
     * Log error
     *
     * @param logMsg Message to log.
     * @param classLocation context of what class an exception was thrown in.
     */
    private fun logError(logMsg: String, classLocation: Class<*>) {
        val logger = LoggerFactory.getLogger(classLocation)
        logger.error(logMsg)
    }

    /**
     * Log error and nameLocation where error occurred.
     *
     * @param logMsg Message to log.
     * @param nameLocation Give a name of the location of where the exception occurred for when class<*> is not available.
     */
    private fun logErrorName(logMsg: String, nameLocation: String) {
        val logger = LoggerFactory.getLogger(nameLocation)
        logger.error(logMsg)
    }
}