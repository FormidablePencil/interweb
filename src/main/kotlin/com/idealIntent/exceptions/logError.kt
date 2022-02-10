package com.idealIntent.exceptions

import org.slf4j.LoggerFactory

/**
 * Log error but don't throw it.
 *
 * @param logMsg Message to log.
 * @param location Give a name of the location logged from.
 */
fun logError(logMsg: String, location: Class<*>) {
    val logger = LoggerFactory.getLogger(location)
    logger.error(logMsg)
}

/**
 * Log error but don't throw it.
 *
 * @param logMsg Message to log.
 * @param location Give a name of the location logged from.
 */
fun logError(logMsg: String, location: String) {
    val logger = LoggerFactory.getLogger(location)
    logger.error(logMsg)
}

fun logInfo(logMsg: String, location: Class<*>) {
    val logger = LoggerFactory.getLogger(location)
    logger.info(logMsg)
}