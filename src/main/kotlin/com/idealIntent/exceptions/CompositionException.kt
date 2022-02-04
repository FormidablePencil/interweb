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
    val code: CompositionCode
    var moreDetails: String? = null

    constructor(
        code: CompositionCode
    ) : super(CompositionCode.getClientMsg(code)) {
        this.code = code
    }

    constructor(
        code: CompositionCode, cause: Exception
    ) : super(CompositionCode.getClientMsg(code), cause) {
        this.code = code
    }

    constructor(
        code: CompositionCode, cause: CompositionException
    ) : super(CompositionCode.getClientMsg(code), cause) {
        this.code = code
        this.moreDetails = cause.moreDetails
    }

    constructor(
        code: CompositionCode, moreDetails: String?
    ) : super(CompositionCode.getClientMsg(code) + appendMoreDetails(moreDetails)) {
        this.code = code
        this.moreDetails = moreDetails
    }

    constructor(
        code: CompositionCode, moreDetails: String?, cause: Exception
    ) : super(CompositionCode.getClientMsg(code) + appendMoreDetails(moreDetails), cause) {
        this.code = code
        this.moreDetails = moreDetails
    }
}
