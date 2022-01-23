package exceptions

import org.slf4j.LoggerFactory

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

    private fun logError(message: String, happenedWhere: Class<*>) {
        val logger = LoggerFactory.getLogger(happenedWhere)
        logger.error(message)
    }

    private fun logErrorName(message: String, name: String) {
        val logger = LoggerFactory.getLogger(name)
        logger.error(message)
    }

    companion object {
        fun logError(message: String, name: String) {
            val logger = LoggerFactory.getLogger(name)
            logger.error(message)
        }
    }
}