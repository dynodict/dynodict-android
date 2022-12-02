package org.dynodict.provider

import org.dynodict.FallbackStrategy
import org.dynodict.Key
import org.dynodict.StringNotFoundException
import org.dynodict.model.DLocale
import org.dynodict.model.DString
import org.dynodict.model.Settings
import org.dynodict.model.metadata.BucketMetadata
import org.dynodict.storage.BucketsStorage
import org.dynodict.storage.MetadataStorage
import java.util.concurrent.ConcurrentHashMap

class StringProviderImpl(
    private val bucketsStorage: BucketsStorage,
    private val metadataStorage: MetadataStorage,
    private val settings: Settings,
    private val defaultBucketsStorage: BucketsStorage,
) : StringProvider {
    private var locale: DLocale? = null
    private val buckets: MutableMap<Key, DString> = ConcurrentHashMap()

    private val defaultBuckets: MutableMap<Key, DString> = ConcurrentHashMap()

    override suspend fun setLocale(locale: DLocale) {
        val metadata = metadataStorage.get() ?: return
        val hasLocale = metadata.languages.contains(locale.value)
        if (!hasLocale) {
            throw IllegalStateException("Locale $locale can not be found")
        }
        val bucketsMetadata = metadata.buckets
        buckets.clear()

        readBucketsFromStorage(bucketsMetadata, locale, bucketsStorage)
        val defaultLanguage = DLocale(metadata.defaultLanguage)
        if (locale != defaultLanguage) {
            readBucketsFromStorage(bucketsMetadata, defaultLanguage, defaultBucketsStorage)
        }
    }

    private suspend fun readBucketsFromStorage(
        bucketsMetadata: List<BucketMetadata>, locale: DLocale, storage: BucketsStorage
    ) {
        val result = mutableMapOf<Key, DString>()
        bucketsMetadata.forEach {
            val map = storage.get(it.name, locale.value, it.editionVersion)?.translations.orEmpty()
                .associateBy { value -> Key(value.key) }
            result.putAll(map)
        }
    }

    override fun get(key: Key): String {
        val dString = buckets[key]
        if (dString != null) return dString.value
        else {
            return when (settings.fallbackStrategy) {
                FallbackStrategy.ThrowException -> {
                    throw StringNotFoundException("String not found for key $key and locale: $locale")
                }
                FallbackStrategy.EmptyString -> {
                    ""
                }
                FallbackStrategy.ReturnDefault -> {
                    // It should never be null for default storage
                    defaultBuckets[key]!!.value
                }
            }
        }
    }
}