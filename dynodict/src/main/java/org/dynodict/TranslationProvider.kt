package org.dynodict

import org.dynodict.FallbackStrategy.*

interface TranslationProvider {
    fun setLocale(locale: TranslationsLocale)

    fun get(translation: Translation): String
}

class TranslationProviderImpl(
    private val storage: ObservableStorage,
    private val defaultStorage: Storage,
    private val settings: Settings
) : TranslationProvider {
    override fun setLocale(locale: TranslationsLocale) {
    }

    override fun get(translation: Translation): String {
        val value = storage.value[translation]

        if (value != null) return value

        return when (settings.fallbackStrategy) {
            ThrowException -> {
                throw TranslationNotFoundException("Translation not found for path: ${translation.absolutePath}")
            }
            EmptyString -> {
                ""
            }
            ReturnDefault -> {
                // It should never be null for default storage
                defaultStorage.value[translation]!!
            }
        }
    }

}





