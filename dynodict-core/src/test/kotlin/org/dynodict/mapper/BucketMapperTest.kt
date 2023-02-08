package org.dynodict.mapper

import org.dynodict.model.bucket.Bucket
import org.dynodict.model.bucket.DString
import org.dynodict.remote.model.bucket.RemoteBucket
import org.dynodict.remote.model.bucket.RemoteDString
import org.dynodict.remote.model.bucket.RemoteParameter
import org.junit.Assert.assertEquals
import org.junit.Test

class BucketMapperTest {

    @Test
    fun `WHEN Remote Bucket is passed THEN all fields are copied`() {
        val bucket = RemoteBucket(
            schemeVersion = 1,
            editionVersion = 2,
            name = "name",
            language = "ua",
            translations = listOf(
                RemoteDString(
                    "Login.Button",
                    "value"
                )
            )
        )

        with(bucket.toDomainBucket()) {
            assertEquals(1, schemeVersion)
            assertEquals(2, editionVersion)
            assertEquals("name", name)
            assertEquals("ua", language)
            assertEquals(1, translations.size)

            with(translations[0]) {
                assertEquals("Login.Button", key)
                assertEquals("value", value)

            }
        }
    }
}