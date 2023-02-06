package org.dynodict.provider

import org.dynodict.*
import org.dynodict.formatter.*
import org.dynodict.model.DLocale
import org.dynodict.model.bucket.DString
import org.dynodict.model.Parameter
import org.dynodict.model.StringKey
import org.dynodict.model.metadata.BucketMetadata
import org.dynodict.model.settings.NotFoundPlaceholderPolicy
import org.dynodict.model.settings.Settings
import org.dynodict.model.settings.StringNotFoundPolicy
import org.dynodict.provider.validator.PlaceholderValidator
import org.dynodict.storage.BucketsStorage
import org.dynodict.storage.MetadataStorage
import org.dynodict.storage.generateBucketName
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

class StringProviderImpl(
    private val bucketsStorage: BucketsStorage,
    private val metadataStorage: MetadataStorage,
    private val settings: Settings,
    private val defaultBucketsStorage: BucketsStorage,
    private val defaultMetadataStorage: MetadataStorage,
    private val placeholderValidator: PlaceholderValidator,
    private val dynodictCallback: DynodictCallback
) : StringProvider {
    private val atomicLocale = AtomicReference<DLocale?>()

    private val buckets: MutableMap<StringKey, DString> = ConcurrentHashMap()
    private val defaultBuckets: MutableMap<StringKey, DString> = ConcurrentHashMap()

    private val formatters: MutableMap<String, DynoDictFormatter<*>?> = mutableMapOf(
        DEFAULT_INT_FORMATTER to IntFormatter(),
        DEFAULT_LONG_FORMATTER to LongFormatter(),
        DEFAULT_FLOAT_FORMATTER to FloatFormatter(),
        DEFAULT_STRING_FORMATTER to StringFormatter()
    )

    override suspend fun setLocale(locale: DLocale) {
        try {
            setStringInternal(locale)
        } catch (ex: LocaleNotFoundException) {
            dynodictCallback.errorOccurred(ex)
        } catch (ex: DefaultMetadataNotFoundException) {
            dynodictCallback.errorOccurred(ex)
        }
    }

    private suspend fun setStringInternal(locale: DLocale) {
        atomicLocale.set(locale)
        val metadata = metadataStorage.get() ?: return
        val hasLocale = metadata.languages.contains(locale.value)
        if (!hasLocale) {
            throw LocaleNotFoundException("Locale $locale can not be found")
        }
        val bucketsMetadata = metadata.buckets

        buckets.clear()
        buckets.putAll(readBucketsFromStorage(bucketsMetadata, locale, bucketsStorage))

        if (defaultBuckets.isEmpty()) {
            val defaultMetadata = defaultMetadataStorage.get()
                ?: throw DefaultMetadataNotFoundException("Can't get default metadata. Make sure a file passed via assets is properly named and contains the required data")

            val defaultLanguage = DLocale(defaultMetadata.defaultLanguage)

            val defaultStrings = readBucketsFromStorage(
                defaultMetadata.buckets,
                defaultLanguage,
                defaultBucketsStorage
            )
            defaultBuckets.putAll(defaultStrings)
        }
    }


    override fun registerFormatter(key: String, value: DynoDictFormatter<*>?) {
        formatters[key] = value
    }

    override fun get(key: StringKey, vararg parameters: Parameter): String {

        val value = buckets[key]?.value ?: handleNotFoundString(key)

        // it is possible that empty string is returned as a fallback policy when string is not found
        if (value.isEmpty()) return value

        try {
            val result = inflateParameters(value, parameters, key)

            // post processing - validate if there any placeholder left
            return placeholderValidator.validate(key, result)
        } catch (ex: Exception) {
            when (ex) {
                is PlaceholderNotFoundException,
                is FormatterNotFoundException,
                is DefaultStringNotFoundException,
                is StringNotFoundException -> {
                    dynodictCallback.errorOccurred(ex)
                }
                else -> throw ex
            }
        }
        return ""
    }

    private fun inflateParameters(
        originalValue: String,
        parameters: Array<out Parameter>,
        key: StringKey
    ): String {
        var result = originalValue
        // inflate parameters
        parameters.forEach { parameter ->
            val formattedParam = prepareParameter(parameter)

            val indexOfParameter = result.indexOf("{${parameter.key}}")
            if (settings.notFoundPlaceholderPolicy == NotFoundPlaceholderPolicy.ThrowException && indexOfParameter < 0) {
                throw PlaceholderNotFoundException("Can not find placeholder for String. $key${parameter.key}")
            }

            result = result.replace("{${parameter.key}}", formattedParam, ignoreCase = true)
        }
        return result
    }


    private suspend fun readBucketsFromStorage(
        bucketsMetadata: List<BucketMetadata>, locale: DLocale, storage: BucketsStorage
    ): Map<StringKey, DString> {
        val result = mutableMapOf<StringKey, DString>()
        bucketsMetadata.forEach {
            val filename = generateBucketName(it.name, locale.value, it.editionVersion)
            val map = storage.get(filename)?.translations.orEmpty()
                .associateBy { value -> StringKey(value.key) }
            result.putAll(map)
        }
        return result
    }

    private fun findFormatter(parameter: Parameter): DynoDictFormatter<*>? {
        if (parameter.format != null) return formatters[parameter.format]
        val id = when (parameter) {
            is Parameter.IntParameter -> DEFAULT_INT_FORMATTER
            is Parameter.LongParameter -> DEFAULT_LONG_FORMATTER
            is Parameter.FloatParameter -> DEFAULT_FLOAT_FORMATTER
            is Parameter.StringParameter -> DEFAULT_STRING_FORMATTER
        }
        return formatters[id]
    }

    private fun prepareParameter(parameter: Parameter): String {
        val formatter = findFormatter(parameter)

        if (formatter == null) {
            throw FormatterNotFoundException("Can't find Formatter with format: ${parameter.format}")
        } else {
            return formatter.format(parameter.value)
        }
    }

    private fun handleNotFoundString(key: StringKey): String {
        when (settings.stringNotFoundPolicy) {
            StringNotFoundPolicy.ThrowException -> {
                throw StringNotFoundException("String not found for key $key and locale: ${atomicLocale.get()}")
            }
            StringNotFoundPolicy.EmptyString -> {
                return ""
            }
            StringNotFoundPolicy.ReturnDefault -> {
                // It should never be null for default storage
                return defaultBuckets[key]?.value
                    ?: throw DefaultStringNotFoundException("Default String can not be found for key:$key")
            }
        }
    }

    companion object {
        const val DEFAULT_INT_FORMATTER = "DefaultIntFormatter"
        const val DEFAULT_LONG_FORMATTER = "DefaultLongFormatter"
        const val DEFAULT_FLOAT_FORMATTER = "DefaultFloatFormatter"
        const val DEFAULT_STRING_FORMATTER = "DefaultStringFormatter"

    }
}