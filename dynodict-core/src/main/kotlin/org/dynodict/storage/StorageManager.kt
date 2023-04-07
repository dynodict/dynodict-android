package org.dynodict.storage

import org.dynodict.model.bucket.Bucket
import org.dynodict.model.bucket.DString
import org.dynodict.model.metadata.BucketMetadata
import org.dynodict.model.metadata.BucketsMetadata

/**
 * General implementation of the manager to take care of storing string persistently
 */
interface StorageManager {
    suspend fun addBucket(bucket: Bucket)
    suspend fun getBucket(metadata: BucketMetadata): Bucket?
    suspend fun getAllForLanguage(language: String): List<DString>
    suspend fun storeBuckets(items: List<Bucket>)

    suspend fun getMetadata(): BucketsMetadata?
    suspend fun storeMetadata(metadata: BucketsMetadata)
    suspend fun removeBuckets(buckets: List<BucketMetadata>)
}

class StorageManagerImpl(
    private val bucketsStorage: BucketsStorage,
    private val metadataStorage: MetadataStorage
) : StorageManager {
    override suspend fun addBucket(bucket: Bucket) {
        bucketsStorage.save(bucket)
    }

    override suspend fun getBucket(metadata: BucketMetadata): Bucket? {
        return bucketsStorage.get(
            generateBucketName(
                metadata.name,
                metadata.language,
                metadata.schemeVersion
            )
        )
    }

    override suspend fun getAllForLanguage(language: String): List<DString> {
        val metadata = metadataStorage.get()
        val languages = metadata?.languages.orEmpty()
        if (!languages.contains(language)) return emptyList()
        val buckets = metadata?.buckets.orEmpty().map { it.copy(language = language) }

        return buckets.flatMap { bucketMetadata ->
            bucketsStorage.getBucket(bucketMetadata)?.translations ?: emptyList()
        }
    }

    private suspend fun BucketsStorage.getBucket(metadata: BucketMetadata): Bucket? {
        with(metadata) {
            val filename = generateBucketName(name, language, schemeVersion)
            return get(filename)
        }
    }

    override suspend fun storeBuckets(items: List<Bucket>) {
        items.forEach {
            bucketsStorage.save(it)
        }
    }

    override suspend fun getMetadata(): BucketsMetadata? {
        return metadataStorage.get()
    }

    override suspend fun storeMetadata(metadata: BucketsMetadata) {
        metadataStorage.save(metadata)
    }

    override suspend fun removeBuckets(buckets: List<BucketMetadata>) {
        buckets.forEach { metadata ->
            bucketsStorage.save(metadata.toBucket())
        }
    }
}

private fun BucketMetadata.toBucket(): Bucket {
    return Bucket(
        editionVersion = editionVersion,
        schemeVersion = schemeVersion,
        language = language,
        name = name,
        translations = emptyList()
    )
}
