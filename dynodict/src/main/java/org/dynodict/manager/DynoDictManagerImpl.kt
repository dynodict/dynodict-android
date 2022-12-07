package org.dynodict.manager

import org.dynodict.DynodictCallback
import org.dynodict.model.DString
import org.dynodict.model.metadata.BucketsMetadata
import org.dynodict.remote.RemoteManager
import org.dynodict.storage.StorageManager


class DynoDictManagerImpl(
    private val remoteManager: RemoteManager,
    val storageManager: StorageManager,
    private val dynodictCallback: DynodictCallback
) : DynoDictManager {

    override suspend fun updateTranslations() {
        val metadata = remoteManager.getMetadata()
        if (metadata == null) {
            dynodictCallback.onErrorOccurred(IllegalStateException("Error during getting the metadata"))
            return
        }

        val result = remoteManager.getStrings(metadata)

        storageManager.storeMetadata(metadata)
        storageManager.storeBuckets(result)
    }

    suspend fun getMetadata(): BucketsMetadata? {
        return storageManager.getMetadata()
    }

    suspend fun getAllForLanguage(language: String): List<DString> {
        return storageManager.getAllForLanguage(language)
    }
}