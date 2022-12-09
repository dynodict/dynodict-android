package org.dynodict.error

class StringNotFoundException(
    message: String?,
    cause: Throwable? = null
) : Exception(message, cause) {
}