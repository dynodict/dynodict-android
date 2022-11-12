package org.dynodict

interface Dynodict {
    fun setLocale(locale: TranslationsLocale)

    fun get(translation: Translation): String
}

enum class TranslationsLocale {
    UA, EN, DA,
    // TODO add extension on Locale
}

data class Settings(val fallbackStrategy: FallbackStrategy)

enum class FallbackStrategy {
    ThrowException, EmptyString, ReturnDefault
}

enum class ExceptionResolution {
    Handled, NotHandled
}
