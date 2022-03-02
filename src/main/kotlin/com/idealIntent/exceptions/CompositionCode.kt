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
    FailedToAssociateAuthorToLayout,
    FailedToCompose,
    ColumnDoesNotExist,
    ProvidedStringInPlaceOfInt,
    CollectionOfRecordsNotFound,
    CompositionRecordIsCorrupt,
    CompositionRecordIsCorrupt2,
    NotPrivilegedToLayout,
    MethodNotNeededThusShouldNotBeCalled,
    ;

    companion object : IServerExceptionCode<CompositionCode>, IApiResponseEnum<CompositionCode> {
        override fun getLogMsg(code: CompositionCode): String {
            return when (code) {
                FailedToGivePrivilege -> "Failed give user privileges."
                NoAuthorIdProvidedToRestrictedResource -> "No id of author provided to restricted resource for validation of privileges."
                FailedToAddRecordToCompositionValidator -> "No records updates which only means that developer failed to add a record to composition validator."
                FailedToAssociateAuthorToLayout -> "Failed to associate author to layout. Author id does not exist perhaps."
                FailedToCompose -> "Failed to compose. Internal cms error."
                ColumnDoesNotExist -> "Failed to handle a column of a record."
                CompositionRecordIsCorrupt -> "Composition is corrupt. Could not find id of collection composition composes."
                CompositionRecordIsCorrupt2 -> "Composition is corrupt. No order rank for composition. Perhaps not associated to any layout."
                MethodNotNeededThusShouldNotBeCalled -> "Attempted to use a method that is not implemented. Method is implemented because it is not needed."

                ServerError -> genericServerError

                FailedToConvertToIntOrderRank,
                EmptyListOfRecordsProvided,
                FailedToAssociateRecordToCollection,
                UserNotPrivileged,
                FailedToFindAuthorByUsername,
                ModifyPermittedToAuthorOfCompositionNotFound,
                IdOfRecordProvidedNotOfComposition,
                CompositionNotFound,
                ProvidedStringInPlaceOfInt,
                CollectionOfRecordsNotFound,
                NotPrivilegedToLayout,
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
                ProvidedStringInPlaceOfInt -> "Provided a string in place of an integer, thus failed to convert String to Int."
                CollectionOfRecordsNotFound -> "Collection of records not found by provided collection id."
                NotPrivilegedToLayout -> "Not privileged to modify composition."

                MethodNotNeededThusShouldNotBeCalled,
                CompositionRecordIsCorrupt2,
                CompositionRecordIsCorrupt,
                ColumnDoesNotExist,
                FailedToAddRecordToCompositionValidator,
                NoAuthorIdProvidedToRestrictedResource,
                FailedToGivePrivilege,
                FailedToAssociateAuthorToLayout,
                ServerError -> genericServerError

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
                ProvidedStringInPlaceOfInt,
                CollectionOfRecordsNotFound,
                CompositionRecordIsCorrupt,
                NotPrivilegedToLayout,
                -> HttpStatusCode.BadRequest

                ServerError,
                FailedToCompose,
                FailedToGivePrivilege,
                NoAuthorIdProvidedToRestrictedResource,
                FailedToAddRecordToCompositionValidator,
                FailedToAssociateAuthorToLayout,
                ColumnDoesNotExist,
                CompositionRecordIsCorrupt2,
                MethodNotNeededThusShouldNotBeCalled,
                -> HttpStatusCode.InternalServerError
            }
        }
    }
}