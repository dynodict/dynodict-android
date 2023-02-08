package org.dynodict.mapper

import org.dynodict.model.metadata.BucketMetadata
import org.dynodict.model.metadata.BucketsMetadata
import org.dynodict.remote.model.metadata.RemoteBucketMetadata
import org.dynodict.remote.model.metadata.RemoteBucketsMetadata
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MetadataMapperTest {

    @Test
    fun `WHEN Remote Metadata is passed THEN all fields are copied in BucketsMetadata`() {
        val remoteBucketsMetadata = RemoteBucketsMetadata(
            defaultLanguage = "ua",
            languages = listOf("ua", "en"),
            buckets = listOf(
                RemoteBucketMetadata(
                    schemeVersion = 1,
                    editionVersion = 2,
                    name = "name",
                    language = "ua",
                )
            )
        )
        with(remoteBucketsMetadata.toDomainMetadata()) {
            assertEquals("ua", defaultLanguage)
            assertTrue(languages.contains("ua"))
            assertTrue(languages.contains("en"))

            assertEquals(1, buckets.size)

            with(buckets[0]) {
                assertEquals(1, schemeVersion)
                assertEquals(2, editionVersion)
                assertEquals("name", name)
                assertEquals("ua", language)
            }
        }
    }

    @Test
    fun `WHEN BucketsMetadata is passed THEN all fields are copied in RemoteMetadata`() {
        val bucketsMetadataBucketsMetadata = BucketsMetadata(
            defaultLanguage = "ua",
            languages = listOf("ua", "en"),
            buckets = listOf(
                BucketMetadata(
                    schemeVersion = 1,
                    editionVersion = 2,
                    name = "name",
                    language = "ua",
                )
            )
        )
        with(bucketsMetadataBucketsMetadata.toRemoteMetadata()) {
            assertEquals("ua", defaultLanguage)
            assertTrue(languages.contains("ua"))
            assertTrue(languages.contains("en"))

            assertEquals(1, buckets.size)

            with(buckets[0]) {
                assertEquals(1, schemeVersion)
                assertEquals(2, editionVersion)
                assertEquals("name", name)
                assertEquals("ua", language)
            }
        }
    }
}