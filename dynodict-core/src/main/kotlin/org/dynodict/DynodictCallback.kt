@file:OptIn(ExperimentalContracts::class)

package org.dynodict

import org.dynodict.ExceptionResolution.Handled
import org.dynodict.ExceptionResolution.NotHandled
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

interface DynodictCallback {
    /**
     * Is invoked when an error happens.
     * @return true exception is handled and should be propagated
     */
    fun onErrorOccurred(ex: Exception): ExceptionResolution
}

//fun <T> DynodictCallback.errorOccurred(ex: Exception, valueIfHandled: T): T {
//    when (onErrorOccurred(ex)) {
//        Handled -> return valueIfHandled
//        NotHandled -> throw ex
//    }
//}

inline fun DynodictCallback.errorOccurred(ex: Exception, onHandled: () -> Unit = {}) {
    when (onErrorOccurred(ex)) {
        Handled -> onHandled.invoke()
        NotHandled -> throw ex
    }
}

enum class ExceptionResolution {
    Handled, NotHandled
}