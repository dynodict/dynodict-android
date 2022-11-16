package org.dynodict

interface StringProvider {
    fun setLocale(locale: DLocale)

    fun get(translation: DString): String
}







