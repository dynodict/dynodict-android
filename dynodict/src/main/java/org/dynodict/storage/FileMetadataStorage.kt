package org.dynodict.storage

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import org.dynodict.model.metadata.BucketsMetadata
import java.io.File

open class FileMetadataStorage(
    folder: File,
    protected val json: Json,
    protected val metadataFile: File = File(folder, NAME)
) : MetadataStorage {
    override suspend fun save(metadata: BucketsMetadata?) {
        createFileIfNeeded(metadataFile).also {
            json.encodeToStream(metadata, it.outputStream())
        }
    }

    override suspend fun get(): BucketsMetadata? {
        createFileIfNeeded(metadataFile).also {
            return json.decodeFromStream<BucketsMetadata>(it.inputStream())
        }
    }

    companion object {
        const val NAME = "metadata.json"
    }
}