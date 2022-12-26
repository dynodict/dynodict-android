package org.dynodict.provider

import org.dynodict.model.DLocale
import org.dynodict.model.Key
import org.dynodict.model.Parameter
import org.dynodict.org.dynodict.formatter.DynoDictFormatter

interface StringProvider {
    suspend fun setLocale(locale: DLocale)

    fun get(key: Key, vararg parameters: Parameter): String

    fun registerFormatter(key: String, value: DynoDictFormatter<*>?)
}







