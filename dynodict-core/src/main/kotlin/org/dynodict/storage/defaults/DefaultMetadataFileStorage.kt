package org.dynodict.storage.defaults

import kotlinx.serialization.StringFormat
import org.dynodict.model.metadata.BucketsMetadata
import org.dynodict.storage.FileMetadataStorage
import org.dynodict.storage.copyAndClose
import org.dynodict.storage.createFileIfNeeded
import java.io.File

class DefaultMetadataFileStorage(
    folder: File,
    json: StringFormat,
    private val defaultDataProvider: DefaultDataProvider,
) : FileMetadataStorage(folder, json, File(folder, DEFAULT_NAME)) {
    override suspend fun get(): BucketsMetadata? {
        val defaultMetadataStream = defaultDataProvider.open(metadataFile.name)
        createFileIfNeeded(metadataFile).also {
            defaultMetadataStream.copyAndClose(it)
        }
        return super.get()
    }

    override suspend fun save(metadata: BucketsMetadata?) {
        throw IllegalStateException("Save should never be called for this kind of Storage")
    }

    companion object {
        const val DEFAULT_NAME = "default_metadata.json"
    }
}