package org.dynodict.provider

import org.dynodict.DefaultStringNotFoundException
import org.dynodict.StringNotFoundException
import org.dynodict.model.DLocale
import org.dynodict.model.DString
import org.dynodict.model.Key
import org.dynodict.model.Parameter
import org.dynodict.model.metadata.BucketMetadata
import org.dynodict.model.settings.FallbackStrategy
import org.dynodict.model.settings.Settings
import org.dynodict.storage.BucketsStorage
import org.dynodict.storage.MetadataStorage
import org.dynodict.storage.generateBucketName
import java.util.concurrent.ConcurrentHashMap

class StringProviderImpl(
    private val bucketsStorage: BucketsStorage,
    private val metadataStorage: MetadataStorage,
    private val settings: Settings,
    private val defaultBucketsStorage: BucketsStorage,
    private val defaultMetadataStorage: MetadataStorage
) : StringProvider {
    private var locale: DLocale? = null
    private val buckets: MutableMap<Key, DString> = ConcurrentHashMap()

    private val defaultBuckets: MutableMap<Key, DString> = ConcurrentHashMap()

    private val defaultFormatters =

    override suspend fun setLocale(locale: DLocale) {
        val metadata = metadataStorage.get() ?: return
        val hasLocale = metadata.languages.contains(locale.value)
        if (!hasLocale) {
            throw IllegalStateException("Locale $locale can not be found")
        }
        val bucketsMetadata = metadata.buckets

        buckets.clear()
        buckets.putAll(readBucketsFromStorage(bucketsMetadata, locale, bucketsStorage))

        if (defaultBuckets.isEmpty()) {

            val defaultMetadata = defaultMetadataStorage.get()
            requireNotNull(defaultMetadata) {
                "Can't get default metadata. Make sure a file passed via assets is properly named and contains the required data"
            }

            val defaultLanguage = DLocale(defaultMetadata.defaultLanguage)

            val defaultStrings = readBucketsFromStorage(
                defaultMetadata.buckets,
                defaultLanguage,
                defaultBucketsStorage
            )
            defaultBuckets.putAll(defaultStrings)
        }
    }

    private suspend fun readBucketsFromStorage(
        bucketsMetadata: List<BucketMetadata>, locale: DLocale, storage: BucketsStorage
    ): Map<Key, DString> {
        val result = mutableMapOf<Key, DString>()
        bucketsMetadata.forEach {
            val filename = generateBucketName(it.name, locale.value, it.editionVersion)
            val map = storage.get(filename)?.translations.orEmpty()
                .associateBy { value -> Key(value.key) }
            result.putAll(map)
        }
        return result
    }

    override fun get(key: Key, vararg parameters: Parameter): String {
        val dString = buckets[key]
        val string = dString?.value ?: handleNotFoundString(key)

        if (string.isEmpty()) return string

        // inflate parameters

    }

    private fun handleNotFoundString(key: Key): String {
        when (settings.fallbackStrategy) {
            FallbackStrategy.ThrowException -> {
                throw StringNotFoundException("String not found for key $key and locale: $locale")
            }
            FallbackStrategy.EmptyString -> {
                return ""
            }
            FallbackStrategy.ReturnDefault -> {
                // It should never be null for default storage
                val value = defaultBuckets[key]?.value

                if (value == null) {
                    throw DefaultStringNotFoundException("Default String can not be found for key:$key")
                }

                return value
            }
        }
    }
}