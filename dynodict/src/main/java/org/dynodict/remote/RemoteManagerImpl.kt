package org.dynodict.remote

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.dynodict.model.Bucket
import org.dynodict.model.metadata.BucketMetadata
import org.dynodict.model.metadata.BucketsMetadata

class RemoteManagerImpl(
    override val settings: RemoteSettings,
    private val json: Json
) : RemoteManager {
    private val client = OkHttpClient()
    private val metadataUrl = settings.endpoint + "metadata.json"

    override suspend fun getMetadata(): BucketsMetadata? {
        val request = Request.Builder().url(metadataUrl).build()
        val response = client.newCall(request).execute()
        val body = response.body?.string()
        if (response.code != 200) {
            return null
        }
        if (body.isNullOrEmpty()) return null

        return json.decodeFromString<BucketsMetadata>(body)
    }

    override suspend fun getStrings(metadata: BucketsMetadata): List<Bucket> {
        val buckets = mutableListOf<Bucket>()

        val requests = metadata.buckets.flatMap { bucket ->
            metadata.languages.map { language ->
                bucket.copy(language = language).toRequest(settings.endpoint, language)
            }
        }

        coroutineScope {
            val deferred = requests.map {
                async {
                    client.newCall(it).execute()
                }
            }

            deferred.forEach {
                val result = it.await()
                val bucketInfo = result.request.tag() as BucketMetadata
                val bucket = parseBucket(result, bucketInfo)
                buckets.add(bucket)
            }
        }
        return buckets
    }

    private fun parseBucket(response: Response, info: BucketMetadata): Bucket {
        val body = response.body?.string()
        if (response.code != 200) {
            throw IllegalStateException("Response code is not 200.")
        }
        if (body.isNullOrEmpty()) throw IllegalStateException("Empty response")
        val decoded: Bucket = json.decodeFromString(body)
        return decoded.copy(name = info.name, language = info.language)
    }

    private fun BucketMetadata.toRequest(url: String, language: String): Request {
        return Request.Builder().url(url + generateFilename(language)).tag(this).build()
    }

    private fun BucketMetadata.generateFilename(language: String): String {
        return "$name-$schemeVersion-$language.json"
    }
}