package org.dynodict

interface DynodictCallback {
    /**
     * Is invoked when an error happens.
     * @return true exception is handled and should be propagated
     */
    fun onErrorOccurred(ex: Exception): ExceptionResolution
}

enum class ExceptionResolution {
    Handled, NotHandled
}