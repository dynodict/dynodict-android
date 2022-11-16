package org.dynodict

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
    fun addBucket(bucketInfo: BucketInfo, bucket: Bucket)
    fun getBucket(bucketInfo: BucketInfo): Bucket?
    fun getAllForLocale(locale: DLocale): List<DString>
    fun storeBuckets(items: Map<BucketInfo, Bucket>)
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
        val items = metadata.buckets.flatMap {
            it.languages.map { language ->
                BucketInfo(
                    editionVersion = it.editionVersion,
                    bucketName = it.name,
                    locale = language,
                    schemeVersion = it.schemeVersion
                )
            }
        }

        val result = remoteManager.getStrings(items)

        storeManager.storeBuckets(result)
    }
}