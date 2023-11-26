package org.dynodict.storage

import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.dynodict.model.metadata.BucketsMetadata
import java.io.File

open class FileMetadataStorage(
    folder: File,
    private val json: StringFormat,
    protected val metadataFile: File = File(folder, NAME),
) : MetadataStorage {

    override suspend fun save(metadata: BucketsMetadata?) {
        createFileIfNeeded(metadataFile).also {
            it.writeText(json.encodeToString(metadata))
        }
    }

    override suspend fun get(): BucketsMetadata? {
        if (!metadataFile.exists()) {
            return null
        }
        return json.decodeFromString<BucketsMetadata>(metadataFile.readText())
    }

    companion object {

        const val NAME = "metadata.json"
    }
}