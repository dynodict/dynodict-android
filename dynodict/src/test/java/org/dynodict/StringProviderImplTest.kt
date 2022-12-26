package org.dynodict

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.dynodict.model.*
import org.dynodict.model.metadata.BucketsMetadata
import org.dynodict.model.settings.FallbackStrategy
import org.dynodict.model.settings.Settings
import org.dynodict.org.dynodict.formatter.DynoDictFormatter
import org.dynodict.org.dynodict.model.settings.ParameterFallbackStrategy
import org.dynodict.provider.StringProvider
import org.dynodict.provider.StringProviderImpl
import org.dynodict.storage.BucketsStorage
import org.dynodict.storage.MetadataStorage
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class StringProviderImplTest {

    private val json = Json {
        ignoreUnknownKeys = true
    }
    private val metadata = json.decodeFromString<BucketsMetadata>(LOGIN_METADATA)

    private val metadataStorageForLogin = mock<MetadataStorage>() {
        onBlocking { get() } doReturn metadata
    }

    @Test
    fun `WHEN predefined parameters are used THEN correct value should be sent`() {
        val storage = BUCKET_JSON.createBucketStorage()
        withStringProvider(storage) {
            // int
            val intResult = get(LOGIN_KEY, Parameter.IntParameter(1, "param1"))
            assertEquals("Log in 1", intResult)

            val longResult = get(LOGIN_KEY, Parameter.LongParameter(1, "param1"))
            assertEquals("Log in 1", longResult)

            val floatResult = get(LOGIN_KEY, Parameter.FloatParameter(1f, "param1"))
            assertEquals("Log in 1.0", floatResult)

            val stringResult = get(LOGIN_KEY, Parameter.StringParameter("1", "param1"))
            assertEquals("Log in 1", stringResult)
        }
    }

    @Test
    fun `WHEN custom formatter is not registered but used THEN exception should be thrown`() {
        val storage = BUCKET_JSON.createBucketStorage()
        withStringProvider(storage) {
            val parameter = Parameter.FloatParameter(10.5f, key = "param1", format = "money")
            try {
                get(LOGIN_KEY, parameter)
                fail()
            } catch (ex: FormatterNotFoundException) {
                // expected
            }
        }
    }

    @Test
    fun `WHEN custom formatter is registered and used THEN correct string should be shown`() {
        val formatter = object : DynoDictFormatter<Long> {
            override fun format(value: Any): String {
                val price = value as Float
                return "$$price"
            }
        }
        val storage = BUCKET_JSON.createBucketStorage()
        withStringProvider(storage) {
            val parameter = Parameter.FloatParameter(10.5f, key = "param1", format = "money")
            registerFormatter("money", formatter)

            // try again with registered Formatter
            val result = get(LOGIN_KEY, parameter)
            assertEquals("Log in $10.5", result)
        }
    }

    @Test
    fun `WHEN get string with unknown key and Strict policy THEN exception should be thrown`() {
        val storage = BUCKET_JSON.createBucketStorage()
        withStringProvider(storage, settings = Settings.Strict) {
            try {
                get(Key("UnknownKey"))
                fail()
            } catch (ex: StringNotFoundException) {
                // expected
            }
        }
    }

    @Test
    fun `WHEN get string with unknown key and EmptyString Policy THEN empty String should be returned`() {
        val storage = BUCKET_JSON.createBucketStorage()
        val settings = Settings(FallbackStrategy.EmptyString, ParameterFallbackStrategy.ReplaceByEmptyString)
        withStringProvider(storage, settings = settings) {
            assertEquals("", get(Key("UnknownKey")))
        }

    }

    @Test
    fun `WHEN get string with unknown key and ReturnDefault Policy THEN Default String should be returned`() {
        val storage = BUCKET_JSON.createBucketStorage()

        val parsedBucket = json.decodeFromString<Bucket>(BUCKET_JSON)
        val changedTranslations = parsedBucket.translations.toMutableList()

        changedTranslations.add(DString(key = "UnknownKey", "Default Value"))

        val defaultBucket = parsedBucket.copy(translations = changedTranslations)
        val defaultStorage = mock<BucketsStorage>() {
            onBlocking { get(any()) } doReturn defaultBucket
        }
        val settings = Settings(FallbackStrategy.ReturnDefault, ParameterFallbackStrategy.ReplaceByEmptyString)
        withStringProvider(storage, settings = settings, defaultBucketsStorage = defaultStorage) {
            assertEquals("Default Value", get(Key("UnknownKey")))
        }
    }

    private fun String.createBucketStorage(): BucketsStorage {
        val bucket = json.decodeFromString<Bucket>(this)
        return mock() {
            onBlocking { get(org.mockito.kotlin.any()) } doReturn bucket
        }
    }

    private fun withStringProvider(
        storage: BucketsStorage,
        metadataStorage: MetadataStorage = metadataStorageForLogin,
        settings: Settings = Settings.Strict,
        defaultBucketsStorage: BucketsStorage = mock(),
        defaultMetadataStorage: MetadataStorage = metadataStorageForLogin,
        presetLocale: Boolean = true,
        action: StringProvider.() -> Unit
    ) {
        StringProviderImpl(storage, metadataStorage, settings, defaultBucketsStorage, defaultMetadataStorage).apply {
            if (presetLocale) {
                runBlocking {
                    // it is required to be
                    setLocale(DLocale("en"))
                }
            }
            action.invoke(this)
        }
    }

    companion object {
        val LOGIN_KEY = Key("LoginScreen.ButtonName")

        const val BUCKET_JSON = """
{
    "editionVersion": 1,
    "schemeVersion": 1,
    "translations": [
        {
            "key": "LoginScreen.ButtonName",
            "value": "Log in {param1}"
        }
    ]
}
        """

        const val LOGIN_METADATA = """
            {
    "defaultLanguage": "en",
    "languages": [
        "en",
        "ua"
    ],
    "buckets": [
        {
            "name": "login",
            "schemeVersion": 1,
            "editionVersion": 1
        }
    ]
}
        """
    }

}