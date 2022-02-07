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
    FailedToCompose,
    FailedToGivePrivilege,
    UserNotPrivileged,
    FailedToFindAuthorByUsername,
    FailedToAssociateRecordToCollection,
    EmptyListOfRecordsProvided,
    NoAuthorIdProvidedToRestrictedResource,
    ;

    companion object : IServerExceptionCode<CompositionCode>, IApiResponseEnum<CompositionCode> {
        override fun getLogMsg(code: CompositionCode): String {
            return when (code) {
                FailedToGivePrivilege -> "Failed give user privileges."
                FailedToCompose -> "Failed to compose."
                FailedToInsertRecord -> "Failed to insert records."
                NoAuthorIdProvidedToRestrictedResource -> "No id of author provided to restricted resource for validation of privileges."

                ServerError -> genericServerError

                EmptyListOfRecordsProvided,
                FailedToAssociateRecordToCollection,
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
                UserNotPrivileged -> "Do not have privileges."
                FailedToFindAuthorByUsername -> "Failed to find author by username."
                FailedToAssociateRecordToCollection -> "Failed to associateRecordToCollection."
                EmptyListOfRecordsProvided -> "Provided an empty list of records."

                NoAuthorIdProvidedToRestrictedResource,
                FailedToGivePrivilege,
                ServerError -> genericServerError

                FailedToInsertRecord,
                FailedToCompose -> contextualServerErrorResponse(code)
            }
        }

        override fun getHttpCode(code: CompositionCode): HttpStatusCode {
            return when (code) {
                UserNotPrivileged,
                FailedToFindAuthorByUsername,
                FailedToAssociateRecordToCollection,
                EmptyListOfRecordsProvided
                -> HttpStatusCode.BadRequest

                ServerError,
                FailedToCompose,
                FailedToGivePrivilege,
                FailedToInsertRecord,
                NoAuthorIdProvidedToRestrictedResource
                -> HttpStatusCode.InternalServerError
            }
        }
    }
}