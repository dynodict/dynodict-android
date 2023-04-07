package org.dynodict.storage

import kotlinx.coroutines.runBlocking
import org.dynodict.model.bucket.Bucket
import org.dynodict.model.metadata.BucketMetadata
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class StorageManagerImplTest {
    @Test
    fun `WHEN getBucket with correct Metadata THEN Bucket should be properly returned`() {

        val bucket = Bucket(
            schemeVersion = 1,
            editionVersion = 2,
            name = "name",
            language = "language",
        )
        val storage = mock<BucketsStorage>() {
            onBlocking { get("login_1_en.json") } doReturn bucket
        }
        runBlocking {

            val result = StorageManagerImpl(storage, mock()).getBucket(
                BucketMetadata(
                    name = "login",
                    schemeVersion = 1,
                    editionVersion = 2,
                    language = "en"
                )
            )

            assertEquals(bucket, result)
        }
    }
}