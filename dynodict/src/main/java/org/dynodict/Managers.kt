package org.dynodict

import org.dynodict.model.Bucket
import org.dynodict.model.DLocale
import org.dynodict.model.DString
import org.dynodict.remote.RemoteManager
import org.dynodict.remote.RemoteSettings

interface DynoDictManager {
    val remoteSettings: RemoteSettings
    suspend fun updateTranslations()
}


/**
 * 3-rd level:
 * It doesn't know anything about retrieving of the Translations. Its responsibility is just to add/replace translation
 * in persistent storage
 */
interface StoreManager {
    fun addBucket(bucketInfo: Bucket, bucket: List<String>)
    fun getBucket(bucket: Bucket): List<DString>?
    fun getAllForLocale(locale: DLocale): List<DString>
    fun storeBuckets(items: List<Bucket>)
}


class DynoDictManagerImpl(
    override val remoteSettings: RemoteSettings,
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

        storeManager.storeBuckets(result)
    }
}