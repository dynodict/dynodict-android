package org.dynodict.manager

import org.dynodict.DynodictCallback
import org.dynodict.EndpointNotSetException
import org.dynodict.MetadataNotFoundException
import org.dynodict.errorOccurred
import org.dynodict.mapper.toDomainBucket
import org.dynodict.mapper.toDomainMetadata
import org.dynodict.mapper.toRemoteMetadata
import org.dynodict.model.metadata.BucketMetadata
import org.dynodict.model.metadata.BucketsMetadata
import org.dynodict.remote.RemoteManager
import org.dynodict.storage.StorageManager

/**
 *
 */
class DynoDictManagerImpl(
    private val remoteManager: RemoteManager,
    // TODO make private after testing
    val storageManager: StorageManager,
    private val dynodictCallback: DynodictCallback
) : DynoDictManager {

    override suspend fun updateStrings() {
        val metadata = updateMetadata() ?: return

        updateBuckets(metadata)
    }

    override suspend fun updateMetadata(): BucketsMetadata? {
        validateRemoteSettings()

        val metadata = remoteManager.getMetadata()?.toDomainMetadata()
        if (metadata == null) {
            val exception = MetadataNotFoundException("Error during getting the metadata")
            dynodictCallback.errorOccurred(exception, onHandled = { return null })
        } else {
            storageManager.storeMetadata(metadata)
        }
        return metadata
    }

    override suspend fun updateBuckets(bucketsMetadata: BucketsMetadata) {
        validateRemoteSettings()
        val result = remoteManager.getStrings(bucketsMetadata.toRemoteMetadata())

        storageManager.storeBuckets(result.map{it.toDomainBucket()})
    }

    override suspend fun removeBuckets(buckets: List<BucketMetadata>) {
        storageManager.removeBuckets(buckets)
    }

    override suspend fun getMetadata(): BucketsMetadata? {
        return storageManager.getMetadata()
    }

    private fun validateRemoteSettings() {
        if (remoteManager.settings.endpoint.isEmpty()) {
            dynodictCallback.errorOccurred(EndpointNotSetException("Can't update strings when endpoint is not passed."))
        }
    }
}