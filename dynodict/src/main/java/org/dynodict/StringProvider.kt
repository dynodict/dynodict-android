package org.dynodict

import org.dynodict.model.DLocale
import org.dynodict.model.DString

interface StringProvider {
    fun setLocale(locale: DLocale)

    fun get(translation: DString): String
}







