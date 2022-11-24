package org.dynodict.remote

import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.dynodict.model.Bucket

interface RemoteManager {
    suspend fun getMetadata(): MetadataResponse?
    suspend fun getStrings(metadata: MetadataResponse): List<Bucket>
}

class RemoteManagerImpl(val remoteSettings: RemoteSettings) : RemoteManager {
    val client = OkHttpClient()
    val metadataUrl = remoteSettings.baseUrl + "metadata.json"

    override suspend fun getMetadata(): MetadataResponse? {
        val request = Request.Builder().url(metadataUrl).build()
        val response = client.newCall(request).execute()
        val body = response.body?.string()
        if (response.code != 200) {
            Log.d(TAG, "Error: $response")
            return null
        }
        Log.d(TAG, "Body: $body")
        if (body.isNullOrEmpty()) return null

        return Json.decodeFromString<MetadataResponse>(body)
    }

    private fun BucketMetadata.toRequest(url: String, language: String): Request {
        return Request.Builder().url(url + generateFilename(language)).tag(this).build()
    }

    override suspend fun getStrings(metadata: MetadataResponse): List<Bucket> {
        val buckets = mutableListOf<Bucket>()

        val requests = metadata.buckets.flatMap { bucket ->
            metadata.languages.map { language ->
                bucket.copy(language = language).toRequest(remoteSettings.baseUrl, language)
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
            Log.d(TAG, "Error: $response")
            throw IllegalStateException("Response code is not 200.")
        }
        if (body.isNullOrEmpty()) throw IllegalStateException("Empty response")
        val decoded: Bucket = Json.decodeFromString(body)
        return decoded.copy(bucketName = info.name, language = info.language)
    }

    companion object {
        const val TAG = "RemoteManagerImpl"
    }

    private fun BucketMetadata.generateFilename(language: String): String {
        return "$name-$schemeVersion-$language.json"
    }
}

