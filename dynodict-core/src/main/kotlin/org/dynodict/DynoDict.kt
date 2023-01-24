@file:Suppress("unused")

package org.dynodict

import kotlinx.serialization.StringFormat
import org.dynodict.formatter.DynoDictFormatter
import org.dynodict.manager.DynoDictManager
import org.dynodict.manager.DynoDictManagerImpl
import org.dynodict.model.DLocale
import org.dynodict.model.Parameter
import org.dynodict.model.StringKey
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
import org.dynodict.storage.defaults.DefaultDataProvider
import org.dynodict.storage.defaults.DefaultMetadataFileStorage
import java.io.File

class DynoDict(
    private val provider: StringProvider,
    // TODO make private after testing
    val manager: DynoDictManager
) : StringProvider, DynoDictManager {

    override suspend fun setLocale(locale: DLocale) {
        provider.setLocale(locale)
    }

    override fun get(key: StringKey, vararg parameters: Parameter): String {
        return provider.get(key, *parameters)
    }

    override fun registerFormatter(key: String, value: DynoDictFormatter<*>?) {
        provider.registerFormatter(key, value)
    }

    override suspend fun updateStrings() {
        manager.updateStrings()
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

    override suspend fun getMetadata(): BucketsMetadata? {
        return manager.getMetadata()
    }

    companion object {
        lateinit var instance: DynoDict
            private set

        fun initWith(
            endpoint: String?,
            converter: StringFormat,
            filesDir: File,
            defaultDataProvider: DefaultDataProvider,
            settings: Settings = Settings.Default
        ): DynoDict {

            val remoteManager = RemoteManagerImpl(RemoteSettings(endpoint.orEmpty()), converter)
            val bucketsStorage = FileBucketsStorage(filesDir, converter)
            val metadataStorage = FileMetadataStorage(filesDir, converter)

            val storageManager = StorageManagerImpl(bucketsStorage, metadataStorage)
            val callback = object : DynodictCallback {
                override fun onErrorOccurred(ex: Exception): ExceptionResolution {
                    return ExceptionResolution.NotHandled
                }

            }
            val manager = DynoDictManagerImpl(remoteManager, storageManager, callback)

            val provider =
                createStringProvider(
                    filesDir,
                    defaultDataProvider,
                    converter,
                    bucketsStorage,
                    metadataStorage,
                    settings
                )

            return DynoDict(
                provider,
                manager
            ).also {
                instance = it
            }
        }

        private fun createStringProvider(
            filesDir: File,
            defaultDataProvider: DefaultDataProvider,
            json: StringFormat,
            bucketsStorage: FileBucketsStorage,
            metadataStorage: FileMetadataStorage,
            settings: Settings
        ): StringProviderImpl {
            val defaultBucketStorage = DefaultBucketsFileStorage(filesDir, json, defaultDataProvider)
            val defaultMetadataStorage = DefaultMetadataFileStorage(filesDir, json, defaultDataProvider)

            return StringProviderImpl(
                bucketsStorage,
                metadataStorage,
                settings,
                defaultBucketStorage,
                defaultMetadataStorage
            )
        }
    }
}
