package org.dynodict.storage

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import org.dynodict.model.Bucket
import java.io.File

class FileBucketsStorage(
    val folder: File, private val json: Json
) : BucketsStorage {
    override suspend fun store(bucket: Bucket) {
        val bucketFile = File(folder, bucket.generateFilename())
        createFileIfNeeded(bucketFile)
        with(bucketFile.outputStream()) {
            json.encodeToStream(bucket, this)
        }
    }

    private fun createFileIfNeeded(file: File) {
        if (!file.exists()) {
            file.createNewFile()
        }
    }

    override suspend fun get(name: String, language: String, schemeVersion: Int): Bucket? {
        val filename = generateBucketName(name, language, schemeVersion)
        val bucketFile = File(folder, filename)
        if (!bucketFile.exists()) return null

        return json.decodeFromStream<Bucket>(bucketFile.inputStream())
    }
}

fun Bucket.generateFilename(): String {
    // TODO handle name+ language as nullable object
    return generateBucketName(name!!, language!!, schemeVersion)
}

private fun generateBucketName(name: String, language: String, schemeVersion: Int): String {
    return name + "_$schemeVersion" + "_$language.json"
}