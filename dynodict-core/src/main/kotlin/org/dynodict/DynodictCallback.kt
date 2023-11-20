@file:OptIn(ExperimentalContracts::class)

package org.dynodict

import org.dynodict.ExceptionResolution.Handled
import org.dynodict.ExceptionResolution.NotHandled
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

interface DynodictCallback {
    /**
     * Is invoked when an error happens.
     * @return [Handled] if the error was handled, [NotHandled] otherwise
     */
    fun onErrorOccurred(ex: Exception): ExceptionResolution
}

inline fun DynodictCallback.errorOccurred(ex: Exception, onHandled: () -> Unit = {}) {
    when (onErrorOccurred(ex)) {
        Handled -> onHandled.invoke()
        NotHandled -> throw ex
    }
}

object EmptyDynodictCallback : DynodictCallback {
    override fun onErrorOccurred(ex: Exception): ExceptionResolution = NotHandled
}

enum class ExceptionResolution {
    Handled, NotHandled
}