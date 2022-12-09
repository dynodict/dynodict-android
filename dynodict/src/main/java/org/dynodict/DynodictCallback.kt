package org.dynodict

interface DynodictCallback {
    /**
     * Is invoked when the error happens.
     * @return true exception is handled and should be propagated
     */
    fun onErrorOccurred(ex: Exception): ExceptionResolution

    fun onStringsUpdated()

    fun onFailedToRetrieveDefaultData(ex: Exception): ExceptionResolution
}

enum class ExceptionResolution {
    Handled, NotHandled
}