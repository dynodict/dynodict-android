package org.dynodict.storage

import org.dynodict.model.Bucket
import org.dynodict.model.metadata.BucketsMetadata

interface BucketsStorage {
    /**
     * @param bucket if translations are empty - removes the file
     */
    suspend fun save(bucket: Bucket)
    suspend fun get(filename: String): Bucket?
}

interface MetadataStorage {
    /**
     *
     */
    suspend fun save(metadata: BucketsMetadata?)
    suspend fun get(): BucketsMetadata?
}