package org.dynodict.manager

import org.dynodict.model.bucket.DString
import org.dynodict.model.metadata.BucketMetadata
import org.dynodict.model.metadata.BucketsMetadata

class EmptyDynoDictManager : DynoDictManager {

    override suspend fun updateStrings() {
        // do nothing
    }

    override suspend fun updateMetadata(): BucketsMetadata? {
        return null
    }

    override suspend fun updateBuckets(bucketsMetadata: BucketsMetadata) {
        // do nothing
    }

    override suspend fun removeBuckets(buckets: List<BucketMetadata>) {
        // do nothing
    }

    override suspend fun getMetadata(): BucketsMetadata? {
        return null
    }

    override suspend fun getAllForLanguage(language: String): List<DString> {
        return emptyList()
    }
}