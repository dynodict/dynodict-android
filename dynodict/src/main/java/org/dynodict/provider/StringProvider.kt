package org.dynodict.provider

import org.dynodict.Key
import org.dynodict.model.DLocale

interface StringProvider {
    suspend fun setLocale(locale: DLocale)

    fun get(key: Key): String
}







