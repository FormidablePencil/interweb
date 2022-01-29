package com.idealIntent.exceptions

import io.ktor.http.*

/**
 * Generic errors. For each enum there is a message and HttpStatusCode.
 *
 * GenericError for reusing similar messages and ease of updating at any time down the line.
 */
enum class GenericError {
    ServerError;

    companion object {
        /**
         * Get message from enum
         *
         * @param enum GenericError enum
         * @return Message corresponding to enum
         */
        fun getMsg(enum: GenericError): String {
            return when (enum) {
                ServerError -> "Server code."
            }
        }

        /**
         * Get http code
         *
         * @param enum GenericError enum
         * @return Http status code corresponding to enum
         */
        fun getHttpCode(enum: GenericError): HttpStatusCode {
            return when (enum) {
                ServerError -> HttpStatusCode.InternalServerError
            }
        }
    }
}