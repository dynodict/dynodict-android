package org.dynodict.formatter

/**
 * Formatter which is used to convert any object to String representation.
 */
interface DynoDictFormatter<T> {
    fun format(value: Any, format: String? = null): String
}