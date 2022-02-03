package com.idealIntent.dtos

/**
 * Response object
 *
 * Attempt to format method returns in these ways
 *  1st as single value; Boolean, String
 *  2nd as single nullable value; Boolean? String?
 *  3rd as data class
 *  4th class with nullable properties - extend with Response return success or fail and for what reasons

 * Why in this order?
 *  To reduce bloat best use the simpler return implementations

 * Why not throw com.idealIntent.exceptions instead?
 *  We won't use com.idealIntent.exceptions because you'll have to span the codebase with try catches you don't
 *  know what a unit of code is throwing as you would with our Return class

 * Currently, we have 4 custom return types
 *  Result - just data
 *  Response - a response are with enum with or without data (passed in the constructor of inherited class)
 *      You can pass the messages up and have parent handle it
 *  ApiResponse - an api response without data
 *  ApiDataResponse - an api response with data

 * @param Enum
 * @param ClassExtendedFrom
 */

open class Response<Enum, ClassExtendedFrom> {
    var code: Enum? = null
    var success: Boolean = true // true by default so that you didn't have to append succeeded() if you didn't want to
}

fun <Enum, ClassExtendedFrom> Response<Enum, ClassExtendedFrom>.failed(code: Enum): ClassExtendedFrom {
    this.success = false
    this.code = code
    return this as ClassExtendedFrom
}

fun <Enum, ClassExtendedFrom> Response<Enum, ClassExtendedFrom>.succeeded(): ClassExtendedFrom {
    this.success = true
    return this as ClassExtendedFrom
}
