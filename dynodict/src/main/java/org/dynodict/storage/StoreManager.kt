package org.dynodict.storage

import org.dynodict.model.Bucket
import org.dynodict.model.DString
import org.dynodict.model.metadata.BucketMetadata
import org.dynodict.model.metadata.BucketsMetadata

/**
 * General implementation of the manager to take care of storing string persistently
 */
interface StoreManager {
    suspend fun addBucket(bucket: Bucket)
    suspend fun getBucket(metadata: BucketMetadata): Bucket?
    suspend fun getAllForLanguage(language: String): List<DString>
    suspend fun storeBuckets(items: List<Bucket>)

    suspend fun getMetadata(): BucketsMetadata?
    suspend fun storeMetadata(metadata: BucketsMetadata)
}

class StoreManagerImpl(
    val bucketsStorage: BucketsStorage,
    val metadataStorage: MetadataStorage
) : StoreManager {
    override suspend fun addBucket(bucket: Bucket) {
        bucketsStorage.store(bucket)
    }

    override suspend fun getBucket(metadata: BucketMetadata): Bucket? {
        metadata.language?.let { language ->
            return bucketsStorage.get(metadata.name, language, metadata.schemeVersion)
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
        return storage.get(name, bucketLang, schemeVersion)
    }

    override suspend fun storeBuckets(items: List<Bucket>) {
        items.forEach {
            bucketsStorage.store(it)
        }
    }

    override suspend fun getMetadata(): BucketsMetadata? {
        return metadataStorage.get()
    }

    override suspend fun storeMetadata(metadata: BucketsMetadata) {
        metadataStorage.store(metadata)
    }
}

