package com.idealIntent.exceptions

import org.slf4j.LoggerFactory

/***
 * A logger that log errors and throw an exception
 *
 * In Julia.main project an exception may propagate to the routing level and responds to the client with a message.
 * @see com.idealIntent.routes.routeRespond
 *
 * @constructor logs and throws error.
 */
class ServerErrorException : Exception {
    constructor(message: String, happenedWhere: Class<*>) : super(message) {
        logError(message, happenedWhere)
    }

    constructor(message: String, happenedWhere: Class<*>, cause: Exception) : super(message, cause) {
        logError(message, happenedWhere)
    }

    constructor(message: String, name: String) : super(message) {
        logErrorName(message, name)
    }

    constructor(message: String, name: String, cause: Exception) : super(message, cause) {
        logErrorName(message, name)
    }

    /**
     * Log error
     *
     * @param message
     * @param happenedWhere context of what class an exception happened
     */
    private fun logError(message: String, happenedWhere: Class<*>) {
        val logger = LoggerFactory.getLogger(happenedWhere)
        logger.error(message)
    }

    /**
     * Log error and name where error occurred
     *
     * @param message
     * @param name just name where the exception occurred for when class<*> is not available
     */
    private fun logErrorName(message: String, name: String) {
        val logger = LoggerFactory.getLogger(name)
        logger.error(message)
    }

    companion object {
        /**
         * Log error
         *
         * Log error but don't throw it.
         *
         * @param message
         * @param name just name where the exception occurred for when class<*> is not available
         */
        fun logError(message: String, name: String) {
            val logger = LoggerFactory.getLogger(name)
            logger.error(message)
        }
    }
}