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
    ModifyPermittedToAuthorOfCompositionNotFound,
    IdOfRecordProvidedNotOfComposition,
    FailedToAddRecordToCompositionValidator,
    FailedToConvertToIntOrderRank,
    CompositionNotFound,
    ;

    companion object : IServerExceptionCode<CompositionCode>, IApiResponseEnum<CompositionCode> {
        override fun getLogMsg(code: CompositionCode): String {
            return when (code) {
                FailedToGivePrivilege -> "Failed give user privileges."
                FailedToCompose -> "Failed to compose."
                FailedToInsertRecord -> "Failed to insert records."
                NoAuthorIdProvidedToRestrictedResource -> "No id of author provided to restricted resource for validation of privileges."
                FailedToAddRecordToCompositionValidator -> "No records updates which only means that developer failed to add a record to composition validator."
                ServerError -> genericServerError

                FailedToConvertToIntOrderRank,
                EmptyListOfRecordsProvided,
                FailedToAssociateRecordToCollection,
                UserNotPrivileged,
                FailedToFindAuthorByUsername,
                ModifyPermittedToAuthorOfCompositionNotFound,
                IdOfRecordProvidedNotOfComposition,
                CompositionNotFound,
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
                ModifyPermittedToAuthorOfCompositionNotFound -> "Either composition of id provided did not exist or author was not permitted to modify"
                IdOfRecordProvidedNotOfComposition -> "Id of record to update is not part of the composition."
                FailedToConvertToIntOrderRank -> "Provided a value for updating order rank that was not a number." // todo - client error but not user error. How should we return this data
                CompositionNotFound -> "Composition not found by composition source id."

                FailedToAddRecordToCompositionValidator,
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
                EmptyListOfRecordsProvided,
                ModifyPermittedToAuthorOfCompositionNotFound,
                IdOfRecordProvidedNotOfComposition,
                FailedToConvertToIntOrderRank,
                CompositionNotFound,
                -> HttpStatusCode.BadRequest

                ServerError,
                FailedToCompose,
                FailedToGivePrivilege,
                FailedToInsertRecord,
                NoAuthorIdProvidedToRestrictedResource,
                FailedToAddRecordToCompositionValidator,
                -> HttpStatusCode.InternalServerError
            }
        }
    }
}