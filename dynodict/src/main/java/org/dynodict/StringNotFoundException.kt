package org.dynodict

class StringNotFoundException(
    message: String?,
    cause: Throwable? = null
) : Exception(message, cause) {
}