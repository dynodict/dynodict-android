package org.dynodict

import android.content.Context
import android.util.Log
import kotlinx.serialization.json.Json
import org.dynodict.manager.DynoDictManager
import org.dynodict.manager.DynoDictManagerImpl
import org.dynodict.model.DLocale
import org.dynodict.model.Key
import org.dynodict.model.metadata.BucketMetadata
import org.dynodict.model.metadata.BucketsMetadata
import org.dynodict.model.settings.Settings
import org.dynodict.provider.StringProvider
import org.dynodict.provider.StringProviderImpl
import org.dynodict.remote.RemoteManagerImpl
import org.dynodict.remote.RemoteSettings
import org.dynodict.storage.FileBucketsStorage
import org.dynodict.storage.FileMetadataStorage
import org.dynodict.storage.StorageManagerImpl
import org.dynodict.storage.defaults.DefaultBucketsFileStorage
import org.dynodict.storage.defaults.DefaultMetadataFileStorage

class DynoDict(
    private val provider: StringProvider,
    // TODO make after testing
    val manager: DynoDictManager,
    private val settings: Settings,
) : StringProvider, DynoDictManager {

    override suspend fun setLocale(locale: DLocale) {
        provider.setLocale(locale)
    }

    override fun get(key: Key): String {
        return provider.get(key)
    }

    override suspend fun updateTranslations() {
        manager.updateTranslations()
    }

    override suspend fun updateMetadata(): BucketsMetadata? {
        return manager.updateMetadata()
    }

    override suspend fun updateBuckets(bucketsMetadata: BucketsMetadata) {
        manager.updateBuckets(bucketsMetadata)
    }

    override suspend fun removeBuckets(buckets: List<BucketMetadata>) {
        manager.removeBuckets(buckets)
    }

    companion object {
        var instance: DynoDict? = null
            private set

        private const val TAG = "DynoDict"

        fun initWith(
            endpoint: String,
            context: Context,
            settings: Settings = Settings.Default
        ): DynoDict {
            val json = Json {
                ignoreUnknownKeys = true
            }

            val remoteManager =
                RemoteManagerImpl(
                    RemoteSettings(endpoint),
                    json
                )
            val bucketsStorage = FileBucketsStorage(context.filesDir, json)
            val metadataStorage = FileMetadataStorage(context.filesDir, json)
            val storageManager = StorageManagerImpl(bucketsStorage, metadataStorage)
            val callback = object : DynodictCallback {
                override fun onErrorOccurred(ex: Exception): ExceptionResolution {
                    Log.d(TAG, "errorOccurred: $ex")
                    return ExceptionResolution.NotHandled
                }

                override fun onStringsUpdated() {
                    Log.d(TAG, "onStringsUpdated: ")
                }

                override fun onFailedToRetrieveDefaultData(ex: Exception): ExceptionResolution {
                    Log.d(TAG, "onFailedToRetrieveDefaultData: $ex")
                    return ExceptionResolution.NotHandled
                }
            }
            val manager = DynoDictManagerImpl(remoteManager, storageManager, callback)

            val defaultBucketStorage = DefaultBucketsFileStorage(context.filesDir, json, context.assets)
            val defaultMetadataStorage = DefaultMetadataFileStorage(context.filesDir, json, context.assets)
            val provider = StringProviderImpl(
                bucketsStorage,
                metadataStorage,
                settings,
                defaultBucketStorage,
                defaultMetadataStorage
            )

            return DynoDict(
                provider,
                manager,
                settings
            )
        }
    }
}
