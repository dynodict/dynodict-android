package org.dynodict

class TranslationNotFoundException(
    message: String?,
    cause: Throwable? = null
) : Exception(message, cause) {
}