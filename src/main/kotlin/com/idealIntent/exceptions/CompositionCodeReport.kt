package com.idealIntent.exceptions

import dtos.IApiResponseEnum
import io.ktor.http.*

// todo - move to library. Used by many. Used as for responses and exceptions.
/**
 * Used with [CompositionExceptionReport] and [CompositionException] to throw an exception and respond to client with
 * a http status code and a user-friendly message.
 */
enum class CompositionCodeReport {
    ServerError,
    FailedToInsertRecord,
    FailedToFindAuthor,
    FailedToCompose;

    companion object : IServerExceptionCode<CompositionCodeReport>, IApiResponseEnum<CompositionCodeReport> {
        override fun getLogMsg(code: CompositionCodeReport): String {
            return when (code) {
                ServerError -> genericServerError
                FailedToInsertRecord,
                FailedToFindAuthor,
                -> {
                    logError(logAttemptToLogClientAsServerError(code), this::class.java)
                    return "Not a server error."
                }
                FailedToCompose -> "Failed to compose."
            }
        }

        override fun getClientMsg(code: CompositionCodeReport): String {
            return when (code) {
                ServerError -> genericServerError
                FailedToInsertRecord -> "Failed to insert records."
                FailedToFindAuthor -> "Failed to find an author to give privileges to."
                FailedToCompose -> contextualServerErrorResponse(code)
            }
        }

        override fun getHttpCode(code: CompositionCodeReport): HttpStatusCode {
            return when (code) {
                FailedToInsertRecord,
                FailedToFindAuthor,
                -> HttpStatusCode.BadRequest
                ServerError,
                FailedToCompose,
                -> HttpStatusCode.InternalServerError
            }
        }
    }
}