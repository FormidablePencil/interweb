package com.idealIntent.exceptions

/**
 * When a custom exception provides [more details][moreDetails] to give an [exception message][Throwable.message] more
 * context, the [more details][moreDetails] gets appended onto [exception message][Throwable.message].
 *
 * @param moreDetails More details to append onto [exception message][Throwable.message].
 */
internal fun appendMoreDetails(moreDetails: String?) = " More details: $moreDetails."