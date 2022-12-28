package org.dynodict.provider

import org.dynodict.formatter.DynoDictFormatter
import org.dynodict.model.DLocale
import org.dynodict.model.Parameter
import org.dynodict.model.StringKey

interface StringProvider {
    suspend fun setLocale(locale: DLocale)

    fun get(key: StringKey, vararg parameters: Parameter): String

    fun registerFormatter(key: String, value: DynoDictFormatter<*>?)
}







