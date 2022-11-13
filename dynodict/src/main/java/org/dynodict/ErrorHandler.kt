package org.dynodict

interface ErrorHandler {
    /**
     * Is invoked when the error happens.
     * @return true exception is handled and should be propagated
     */
    fun onErrorOccurred(ex: Exception): ExceptionResolution
}

enum class ExceptionResolution {
    Handled, NotHandled
}