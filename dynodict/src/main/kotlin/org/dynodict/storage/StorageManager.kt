package org.dynodict.storage

import org.dynodict.model.Bucket
import org.dynodict.model.DString
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
        metadata.language?.let { language ->
            return bucketsStorage.get(
                generateBucketName(
                    metadata.name,
                    language,
                    metadata.schemeVersion
                )
            )
        }
        return null
    }

    override suspend fun getAllForLanguage(language: String): List<DString> {
        val metadata = metadataStorage.get()
        val languages = metadata?.languages.orEmpty()
        if (!languages.contains(language)) return emptyList()
        val buckets = metadata?.buckets.orEmpty().map { it.copy(language = language) }

        return buckets.flatMap { bucketMetadata ->
            bucketMetadata.getBucket(bucketsStorage)?.translations ?: emptyList()
        }
    }

    private suspend fun BucketMetadata.getBucket(storage: BucketsStorage): Bucket? {
        val bucketLang = language ?: return null
        val filename = generateBucketName(name, bucketLang, schemeVersion)
        return storage.get(filename)
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
