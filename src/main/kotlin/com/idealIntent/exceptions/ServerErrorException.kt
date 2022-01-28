package com.idealIntent.exceptions

import org.slf4j.LoggerFactory


/***
 * A logger will log errors and may throw an exception.
 * In Julia.main project is caught at the routing level and responds to the client.
 * @see com.idealIntent.routes.routeRespond
 *
 * @constructor logs and throws error
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
     * @param message for catch block to get. Typically, this exception will be caught in the routing and will return the message
     * @param happenedWhere context of what class an exception happened
     */
    private fun logError(message: String, happenedWhere: Class<*>) {
        val logger = LoggerFactory.getLogger(happenedWhere)
        logger.error(message)
    }

    /**
     * Log error and name where error occurred
     *
     * @param message for catch block to get. Typically, this exception will be caught in the routing and will return the message
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
         * Log error but doesn't throw it.
         *
         * @param message
         * @param name
         */
        fun logError(message: String, name: String) {
            val logger = LoggerFactory.getLogger(name)
            logger.error(message)
        }
    }
}