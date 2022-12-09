package org.dynodict

import org.dynodict.manager.DynoDictManager
import org.dynodict.model.DLocale
import org.dynodict.model.Key
import org.dynodict.model.metadata.BucketMetadata
import org.dynodict.model.metadata.BucketsMetadata
import org.dynodict.model.settings.Settings
import org.dynodict.provider.StringProvider

class Dynodict(
    private val provider: StringProvider,
    private val manager: DynoDictManager,
    private val settings: Settings,
) : StringProvider, DynoDictManager {

    override suspend fun setLocale(locale: DLocale) {
        provider.setLocale(locale)
    }

    override fun get(key: Key): String {
        return provider.get(key)
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

    override suspend fun updateTranslations() {
        manager.updateTranslations()
    }

    override suspend fun updateMetadata(): BucketsMetadata? {
        TODO("Not yet implemented")
    }

    override suspend fun updateBuckets(bucketsMetadata: BucketsMetadata) {
        TODO("Not yet implemented")
    }

    override suspend fun removeBuckets(buckets: List<BucketMetadata>) {
        TODO("Not yet implemented")
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

