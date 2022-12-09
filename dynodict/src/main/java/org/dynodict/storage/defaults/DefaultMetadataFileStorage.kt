package org.dynodict.storage.defaults

import android.content.res.AssetManager
import kotlinx.serialization.json.Json
import org.dynodict.model.metadata.BucketsMetadata
import org.dynodict.storage.FileMetadataStorage
import org.dynodict.storage.copyAndClose
import org.dynodict.storage.createFileIfNeeded
import java.io.File

class DefaultMetadataFileStorage(
    folder: File,
    json: Json,
    private val assetsManager: AssetManager
) : FileMetadataStorage(folder, json, File(folder, DEFAULT_NAME)) {
    override suspend fun get(): BucketsMetadata? {
        val defaultMetadataStream = assetsManager.open(metadataFile.name)
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