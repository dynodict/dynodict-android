package org.dynodict.manager

import org.dynodict.model.metadata.BucketMetadata
import org.dynodict.model.metadata.BucketsMetadata

/**
 * Manager which is used to update the strings or buckets from server and store it locally
 */
interface DynoDictManager {
    /**
     * Update all strings. It automatically downloads Metadata and based on it - all buckets
     */
    suspend fun updateStrings()

    /**
     * Update metadata from Remote Server
     */
    suspend fun updateMetadata(): BucketsMetadata?

    /**
     * Update only list of buckets based on the metadata
     */
    suspend fun updateBuckets(bucketsMetadata: BucketsMetadata)

    /**
     * Remove list of buckets from local storage
     */
    suspend fun removeBuckets(buckets: List<BucketMetadata>)

    /**
     * Retrieve metadata available in local storage
     */
    suspend fun getMetadata(): BucketsMetadata?
}