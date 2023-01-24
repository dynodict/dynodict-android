package org.dynodict.storage

import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.dynodict.model.Bucket
import java.io.File
import java.nio.charset.Charset

open class FileBucketsStorage(
    protected val folder: File,
    protected val json: StringFormat
) : BucketsStorage {
    override suspend fun save(bucket: Bucket) {
        val bucketFile = File(folder, bucket.generateFilename())
        createFileIfNeeded(bucketFile)
        bucketFile.writeText(json.encodeToString(bucket), Charset.defaultCharset())
    }

    override suspend fun get(filename: String): Bucket? {
        val bucketFile = File(folder, filename)
        if (!bucketFile.exists()) return null

        return json.decodeFromString<Bucket>(bucketFile.readText())
    }
}

fun Bucket.generateFilename(): String {
    // TODO handle name + language as nullable object
    return generateBucketName(name!!, language!!, schemeVersion)
}
