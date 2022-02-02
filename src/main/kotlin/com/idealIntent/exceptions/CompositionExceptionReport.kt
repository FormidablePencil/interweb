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
        code: CompositionCodeReport, happenedWhere: Class<*>,
    ) : super(CompositionCodeReport.getLogMsg(code), CompositionCodeReport.getClientMsg(code), happenedWhere)

    constructor(
        code: CompositionCodeReport, happenedWhere: Class<*>, cause: Exception,
    ) : super(CompositionCodeReport.getLogMsg(code), CompositionCodeReport.getClientMsg(code), happenedWhere, cause)

    constructor(
        code: CompositionCodeReport, moreDetails: String, happenedWhere: Class<*>,
    ) : super(CompositionCodeReport.getLogMsg(code), CompositionCodeReport.getClientMsg(code) + appendMoreDetails(moreDetails),
        happenedWhere)

    constructor(
        code: CompositionCodeReport, moreDetails: String, happenedWhere: Class<*>, cause: Exception,
    ) : super(CompositionCodeReport.getLogMsg(code), CompositionCodeReport.getClientMsg(code) + appendMoreDetails(moreDetails),
        happenedWhere, cause)
}