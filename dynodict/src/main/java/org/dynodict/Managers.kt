package org.dynodict

import org.dynodict.model.DString
import org.dynodict.model.metadata.BucketsMetadata
import org.dynodict.remote.RemoteManager
import org.dynodict.storage.StoreManager

interface DynoDictManager {
    suspend fun updateTranslations()
    suspend fun getMetadata(): BucketsMetadata?
    suspend fun getAllForLanguage(language: String): List<DString>
}

class DynoDictManagerImpl(
//    override val remoteSettings: RemoteSettings,
    val remoteManager: RemoteManager,
    val storeManager: StoreManager,
    val errorHandler: ErrorHandler
) : DynoDictManager {

    override suspend fun updateTranslations() {
        val metadata = remoteManager.getMetadata()
        if (metadata == null) {
            errorHandler.onErrorOccurred(IllegalStateException("Error during getting the metadata"))
            return
        }

        val result = remoteManager.getStrings(metadata)

        storeManager.storeMetadata(metadata)
        storeManager.storeBuckets(result)
    }

    override suspend fun getMetadata(): BucketsMetadata? {
        return storeManager.getMetadata()
    }

    override suspend fun getAllForLanguage(language: String): List<DString> {
        return storeManager.getAllForLanguage(language)
    }
}