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
import org.dynodict.provider.validator.PlaceholderValidatorImpl
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
    private val manager: DynoDictManager
) : StringProvider by provider, DynoDictManager by manager {

    companion object {
        lateinit var instance: DynoDict
            private set

        fun initWith(
            endpoint: String?,
            converter: StringFormat,
            filesDir: File,
            defaultDataProvider: DefaultDataProvider,
            callback: DynodictCallback,
            settings: Settings,
        ): DynoDict {

            val remoteManager = RemoteManagerImpl(RemoteSettings(endpoint.orEmpty()), converter)
            val bucketsStorage = FileBucketsStorage(filesDir, converter)
            val metadataStorage = FileMetadataStorage(filesDir, converter)

            val storageManager = StorageManagerImpl(bucketsStorage, metadataStorage)
            val manager = DynoDictManagerImpl(remoteManager, storageManager, callback)

            val provider =
                createStringProvider(
                    filesDir,
                    defaultDataProvider,
                    converter,
                    bucketsStorage,
                    metadataStorage,
                    settings,
                    callback
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
            serializer: StringFormat,
            bucketsStorage: FileBucketsStorage,
            metadataStorage: FileMetadataStorage,
            settings: Settings,
            dynodictCallback: DynodictCallback
        ): StringProviderImpl {
            val defaultBucketStorage =
                DefaultBucketsFileStorage(filesDir, serializer, defaultDataProvider)
            val defaultMetadataStorage =
                DefaultMetadataFileStorage(filesDir, serializer, defaultDataProvider)

            val placeholderValidator = PlaceholderValidatorImpl(settings.redundantPlaceholderPolicy)
            return StringProviderImpl(
                bucketsStorage,
                metadataStorage,
                settings,
                defaultBucketStorage,
                defaultMetadataStorage,
                placeholderValidator,
                dynodictCallback
            )
        }
    }
}
