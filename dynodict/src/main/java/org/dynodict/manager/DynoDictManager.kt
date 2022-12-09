package org.dynodict.manager

import org.dynodict.model.metadata.BucketMetadata
import org.dynodict.model.metadata.BucketsMetadata

interface DynoDictManager {
    suspend fun updateTranslations()
    suspend fun updateMetadata(): BucketsMetadata?
    suspend fun updateBuckets(bucketsMetadata: BucketsMetadata)
    suspend fun removeBuckets(buckets: List<BucketMetadata>)
}