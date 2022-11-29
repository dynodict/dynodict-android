package org.dynodict.storage

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import org.dynodict.model.metadata.BucketsMetadata
import java.io.File

class FileMetadataStorage(
    private val folder: File,
    private val json: Json
) : MetadataStorage {
    override suspend fun store(metadata: BucketsMetadata) {
        createFileIfNeeded(folder).apply {
            json.encodeToStream(metadata, this.outputStream())
        }
    }

    private fun createFileIfNeeded(folder: File): File {
        val metadataFile = File(folder, NAME)
        if (!metadataFile.exists()) {
            metadataFile.createNewFile()
        }
        return metadataFile
    }

    override suspend fun get(): BucketsMetadata? {
        createFileIfNeeded(folder).apply {
            return json.decodeFromStream<BucketsMetadata>(inputStream())
        }
    }

    companion object {
        const val NAME = "metadata.json"
    }
}