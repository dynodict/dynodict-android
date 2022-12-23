package org.dynodict

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.dynodict.model.Bucket
import org.dynodict.model.DLocale
import org.dynodict.model.Key
import org.dynodict.model.Parameter
import org.dynodict.model.metadata.BucketsMetadata
import org.dynodict.model.settings.Settings
import org.dynodict.provider.StringProviderImpl
import org.dynodict.storage.BucketsStorage
import org.dynodict.storage.MetadataStorage
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class StringProviderImplTest {
    @Test
    fun `inflate one int parameter`() {
        val json = Json {
            ignoreUnknownKeys = true
        }
        val bucket = Json.decodeFromString<Bucket>(BUCKET_JSON)
        val metadata = Json.decodeFromString<BucketsMetadata>(JSON_METADATA)

        val bucketStorage = mock<BucketsStorage>() {
            onBlocking { get(any()) } doReturn bucket
        }
        val metadataStorage = mock<MetadataStorage>() {
            onBlocking { get() } doReturn metadata
        }

        with(StringProviderImpl(bucketStorage, metadataStorage, Settings.Strict, bucketStorage, metadataStorage)) {
            runBlocking {

                setLocale(DLocale("en"))

                val result = get(
                    Key("LoginScreen.ButtonName"),
                    Parameter.StringParameter("StringParam", "param1"),
                    Parameter.IntParameter(10, "param2")
                )

                assertEquals("Log in StringParam (10)", result)
            }
        }
    }

    companion object {
        const val BUCKET_JSON = """
{
    "editionVersion": 1,
    "schemeVersion": 1,
    "translations": [
        {
            "key": "LoginScreen.ButtonName",
            "value": "Log in {param1} ({param2})"
        }
    ]
}
        """

        const val JSON_METADATA = """
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