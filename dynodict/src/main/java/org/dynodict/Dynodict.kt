package org.dynodict

import org.dynodict.model.DLocale
import org.dynodict.model.DString
import org.dynodict.model.Settings

class Dynodict(
    val provider: StringProvider,
    val manager: DynoDictManager,
    val settings: Settings
) : StringProvider {

    override fun setLocale(locale: DLocale) {
        provider.setLocale(locale)
    }

    override fun get(string: DString): String {
        return provider.get(string)
    }

    companion object {
        var instance: Dynodict? = null

//        fun initWith(
//            endpoint: String, settings: Settings = Settings.Default
//        ): Dynodict {
////            val storage = InMemoryObservableStorage()
////            val defaultStorage = InMemoryObservableStorage()
////            val provider = TranslationProviderImpl(storage, defaultStorage, settings)
////            return Dynodict(provider, FakeTranslationManager(storage), settings).also {
////                instance = it
////            }
//        }
    }
}

//class FakeTranslationManager(
//    private val storage: Storage, private val data: Map<Translation, String> = emptyMap()
//) : DynoDictManager {
//
//    override fun setEndpoint(endpoint: String) {
//        /* No op */
//    }
//
//    override suspend fun updateTranslations() {
//        storage.value = data
//    }
//}

