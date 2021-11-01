package exceptions

import org.slf4j.LoggerFactory

class ServerErrorException : Exception {

    constructor(message: String, happenedWhere: Class<*>) : super(message) {
        logError(message, happenedWhere)
    }

    constructor(message: String, happenedWhere: Class<*>, cause: Exception) : super(message, cause) {
        logError(message, happenedWhere)
    }

    private fun logError(message: String, happenedWhere: Class<*>) {
        val logger = LoggerFactory.getLogger(happenedWhere)
        logger.error(message)
    }
}