package com.idealIntent.exceptions

/**
 * Composition exception and logger.
 *
 * Used for in 2 different places.When some logic should never fail but given some
 * edge case it might and when some logic should never fail but don't want to have a null return type.
 * E.g [batchInsertRecordsToNewCollection][com.idealIntent.repositories.collections.ICollectionStructure.batchInsertRecordsToNewCollection].
 * It should never fail new collection of records because we have determined that inserting records and collection, and
 * associating records to collection should never fail.
 *
 * This exception is used for in general in composition logic. The exception will bubble up as [ServerErrorException] to
 * [routeRespond][com.idealIntent.routes.routeRespond] with a message converted by enum to a user-friendly message.
 *
 * @constructor Logs error.
 */
class CompositionExceptionReport : ServerErrorException {
    constructor(
        code: CompositionCode, happenedWhere: Class<*>,
    ) : super(CompositionCode.getLogMsg(code), CompositionCode.getClientMsg(code), happenedWhere)

    constructor(
        code: CompositionCode, happenedWhere: Class<*>, cause: Exception,
    ) : super(CompositionCode.getLogMsg(code), CompositionCode.getClientMsg(code), happenedWhere, cause)

    constructor(
        code: CompositionCode, moreDetails: String, happenedWhere: Class<*>,
    ) : super(CompositionCode.getLogMsg(code), CompositionCode.getClientMsg(code) + appendMoreDetails(moreDetails),
        happenedWhere)

    constructor(
        code: CompositionCode, moreDetails: String, happenedWhere: Class<*>, cause: Exception,
    ) : super(CompositionCode.getLogMsg(code), CompositionCode.getClientMsg(code) + appendMoreDetails(moreDetails),
        happenedWhere, cause)
}