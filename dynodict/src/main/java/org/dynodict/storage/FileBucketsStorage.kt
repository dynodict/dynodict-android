package org.dynodict.storage

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import org.dynodict.model.Bucket
import java.io.File

open class FileBucketsStorage(
    protected val folder: File,
    protected val json: Json
) : BucketsStorage {
    override suspend fun save(bucket: Bucket) {
        val bucketFile = File(folder, bucket.generateFilename())
        createFileIfNeeded(bucketFile)
        with(bucketFile.outputStream()) {
            json.encodeToStream(bucket, this)
        }
    }

    override suspend fun get(filename: String): Bucket? {
        val bucketFile = File(folder, filename)
        if (!bucketFile.exists()) return null

        return json.decodeFromStream<Bucket>(bucketFile.inputStream())
    }
}

fun Bucket.generateFilename(): String {
    // TODO handle name + language as nullable object
    return generateBucketName(name!!, language!!, schemeVersion)
}
