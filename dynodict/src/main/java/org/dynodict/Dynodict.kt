package org.dynodict

import org.dynodict.manager.DynoDictManager
import org.dynodict.model.DLocale
import org.dynodict.model.Settings
import org.dynodict.provider.StringProvider

class Dynodict(
    val provider: StringProvider,
    val manager: DynoDictManager,
    val settings: Settings
) : StringProvider {

    override suspend fun setLocale(locale: DLocale) {
        provider.setLocale(locale)
    }

    override fun get(key: Key): String {
        return ""
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

