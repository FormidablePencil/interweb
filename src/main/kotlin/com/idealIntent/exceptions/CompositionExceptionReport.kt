package com.idealIntent.exceptions

/**
 * Composition exception and logger.
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