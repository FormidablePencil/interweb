package com.idealIntent.exceptions

/**
 * Composition exception and logger.
 *
 * Any exception with "Report" in the name is not meant to be caught tell the very end. In this case
 * the exception will bubble up to [routeRespond][com.idealIntent.routes.routeRespond] and will response with a message
 * corresponding to the error code. Likely it will be a generic error message though [CompositionCode] determines this.
 *
 * Used for when there would obviously be a bug in the code opposed to [CompositionException].

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