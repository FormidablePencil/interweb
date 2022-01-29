package com.idealIntent.helpers

interface IFromIntToEnum {
    val value: Int
}

/**
 * Convert int to enum type. Used as a companion object in enum.
 *
 * @param T Enum class
 * @property value To convert from to enum equivalent
 * @property values Enum entries
 */
fun <T : IFromIntToEnum> fromIntToEnum(value: Int, values: Array<T>) = values.first { it.value == value }
