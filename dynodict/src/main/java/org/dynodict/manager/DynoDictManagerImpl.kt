package org.dynodict.manager

import org.dynodict.DynodictCallback
import org.dynodict.ExceptionResolution.Handled
import org.dynodict.ExceptionResolution.NotHandled
import org.dynodict.model.metadata.BucketMetadata
import org.dynodict.model.metadata.BucketsMetadata
import org.dynodict.remote.RemoteManager
import org.dynodict.storage.StorageManager


class DynoDictManagerImpl(
    private val remoteManager: RemoteManager,
    private val storageManager: StorageManager,
    private val dynodictCallback: DynodictCallback
) : DynoDictManager {

    override suspend fun updateTranslations() {
        val metadata = updateMetadata() ?: return

        updateBuckets(metadata)
    }

    override suspend fun updateMetadata(): BucketsMetadata? {
        val metadata = remoteManager.getMetadata()
        if (metadata == null) {
            val exception = IllegalStateException("Error during getting the metadata")
            when (dynodictCallback.onErrorOccurred(exception)) {
                Handled -> {
                    // just return the function since this scenario is already handled
                    return null
                }
                NotHandled -> {
                    throw exception
                }
            }
        }

        storageManager.storeMetadata(metadata)
        return metadata
    }

    override suspend fun updateBuckets(bucketsMetadata: BucketsMetadata) {
        val result = remoteManager.getStrings(bucketsMetadata)

        storageManager.storeBuckets(result)
    }

    override suspend fun removeBuckets(buckets: List<BucketMetadata>) {
        storageManager.removeBuckets(buckets)
    }
}