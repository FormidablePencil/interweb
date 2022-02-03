package com.idealIntent.exceptions

import com.idealIntent.exceptions.CompositionCode.*
import dtos.IApiResponseEnum
import io.ktor.http.*

// todo - move to library. Used by many. Used as for responses and exceptions.
/**
 * Server error response messages.
 *
 * Used with [CompositionExceptionReport] and [CompositionException] to throw an exception and respond to client with
 * a http status code and a user-friendly message.
 *
 * @property FailedToFindAuthor response data type string - username
 * @property FailedToGivePrivilege response data type string - username
 * @property FailedAtAuthorLookup response data type string - username
 */
enum class CompositionCode {
    ServerError,
    FailedToInsertRecord,
    FailedToFindAuthor,
    FailedToCompose,
    FailedToGivePrivilege,
    FailedAtAuthorLookup;

    companion object : IServerExceptionCode<CompositionCode>, IApiResponseEnum<CompositionCode> {
        override fun getLogMsg(code: CompositionCode): String {
            return when (code) {
                FailedToGivePrivilege -> "Failed give user privileges."
                FailedToCompose -> "Failed to compose."
                FailedToInsertRecord -> "Failed to insert records."

                ServerError -> genericServerError
                FailedToFindAuthor,
                FailedAtAuthorLookup,
                -> {
                    logError(logAttemptToLogClientAsServerError(code), this::class.java)
                    return "Not an internal error."
                }
            }
        }

        override fun getClientMsg(code: CompositionCode): String {
            return when (code) {
                FailedToFindAuthor -> "Failed to find an author to give privileges to."
                FailedAtAuthorLookup -> "Author by id does not exist."

                FailedToGivePrivilege,
                ServerError -> genericServerError

                FailedToInsertRecord,
                FailedToCompose -> contextualServerErrorResponse(code)
            }
        }

        override fun getHttpCode(code: CompositionCode): HttpStatusCode {
            return when (code) {
                FailedToInsertRecord,
                FailedToFindAuthor,
                FailedAtAuthorLookup,
                -> HttpStatusCode.BadRequest
                ServerError,
                FailedToCompose,
                FailedToGivePrivilege,
                -> HttpStatusCode.InternalServerError
            }
        }
    }
}