package org.dynodict

class Dynodict(
    val provider: TranslationProvider, val manager: TranslationManager, val settings: Settings
) : TranslationProvider {

    override fun setLocale(locale: TranslationsLocale) {
        provider.setLocale(locale)
    }

    override fun get(translation: Translation): String {
        return provider.get(translation)
    }

    companion object {
        var instance: Dynodict? = null

        fun with(
            endpoint: String, settings: Settings = Settings.Default
        ): Dynodict {
            val storage = InMemoryObservableStorage()
            val defaultStorage = InMemoryObservableStorage()
            val provider = TranslationProviderImpl(storage, defaultStorage, settings)
            return Dynodict(provider, FakeTranslationManager(storage), settings)
        }
    }
}

class FakeTranslationManager(
    private val storage: Storage, private val data: Map<Translation, String> = emptyMap()
) : SimpleManager {

    override fun setEndpoint(endpoint: String) {
        /* No op */
    }

    override fun updateTranslations() {
        storage.value = data
    }
}

