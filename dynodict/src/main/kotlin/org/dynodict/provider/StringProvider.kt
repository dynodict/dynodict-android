package org.dynodict.provider

import org.dynodict.model.DLocale
import org.dynodict.model.Key
import org.dynodict.model.Parameter

interface StringProvider {
    suspend fun setLocale(locale: DLocale)

    fun get(key: Key, vararg parameters: Parameter): String
}







