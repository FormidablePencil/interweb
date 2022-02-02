package com.idealIntent.exceptions

/**
 * When thrown, the exception must be caught and handled in code.
 *
 * @property message Message is always a client friendly-response.
 * @property code Used in a switch in catch statement to handle exception appropriately.
 * @constructor Saves to [code] to received enum and passes to [Throwable.message] a client-friendly message corresponding
 * to given enum. moreDetails is passed in constructor, message of moreDetails gets appended[] to client-friendly message.
 */
class CompositionException : Exception {
    val code: CompositionCodeReport

    constructor(
        code: CompositionCodeReport,
    ) : super(CompositionCodeReport.getClientMsg(code)) {
        this.code = code
    }

    constructor(
        code: CompositionCodeReport,
        moreDetails: String,
    ) : super(CompositionCodeReport.getClientMsg(code) + appendMoreDetails(moreDetails)) {
        this.code = code
    }
}
