package com.idealIntent.exceptions

import dtos.IApiResponseEnum
import io.ktor.http.*

// todo - move to library. Used by many. Used as for responses and exceptions.
/**
 * Server error response messages.
 *
 * Used with [CompositionExceptionReport] and [CompositionException] to throw an exception and respond to client with
 * a http status code and a user-friendly message.
 */
enum class CompositionCode {
    ServerError,
    FailedToInsertRecord,
    FailedToFindAuthor,
    FailedToCompose,
    FailedToGivePrivilege,
    UserNotPrivileged,
    FailedToFindAuthorByUsername,
    FailedToAssociateRecordToCollection,
    NoRecordsProvided;

    companion object : IServerExceptionCode<CompositionCode>, IApiResponseEnum<CompositionCode> {
        override fun getLogMsg(code: CompositionCode): String {
            return when (code) {
                FailedToGivePrivilege -> "Failed give user privileges."
                FailedToCompose -> "Failed to compose."
                FailedToInsertRecord -> "Failed to insert records."

                ServerError -> genericServerError

                NoRecordsProvided,
                FailedToAssociateRecordToCollection,
                FailedToFindAuthor,
                UserNotPrivileged,
                FailedToFindAuthorByUsername,
                -> {
                    logError(logAttemptToLogClientAsServerError(code), this::class.java)
                    return "Not an internal error."
                }
            }
        }

        override fun getClientMsg(code: CompositionCode): String {
            return when (code) {
                FailedToFindAuthor -> "Failed to find an author to give privileges to."
                UserNotPrivileged -> "Do not have privileges."
                FailedToFindAuthorByUsername -> "Failed to find author by username."
                FailedToAssociateRecordToCollection -> "Failed to associateRecordToCollection."
                NoRecordsProvided -> "No records provided."

                FailedToGivePrivilege,
                ServerError -> genericServerError

                FailedToInsertRecord,
                FailedToCompose -> contextualServerErrorResponse(code)
            }
        }

        override fun getHttpCode(code: CompositionCode): HttpStatusCode {
            return when (code) {
                FailedToFindAuthor,
                UserNotPrivileged,
                FailedToFindAuthorByUsername,
                FailedToAssociateRecordToCollection,
                NoRecordsProvided
                -> HttpStatusCode.BadRequest

                ServerError,
                FailedToCompose,
                FailedToGivePrivilege,
                FailedToInsertRecord,
                -> HttpStatusCode.InternalServerError
            }
        }
    }
}