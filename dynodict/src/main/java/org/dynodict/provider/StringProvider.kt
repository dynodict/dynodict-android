package org.dynodict.provider

import org.dynodict.model.DLocale
import org.dynodict.model.Key

interface StringProvider {
    suspend fun setLocale(locale: DLocale)

    fun get(key: Key): String
}







