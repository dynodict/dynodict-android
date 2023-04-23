package org.dynodict.remote

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromString
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.dynodict.remote.model.bucket.RemoteBucket
import org.dynodict.remote.model.metadata.RemoteBucketMetadata
import org.dynodict.remote.model.metadata.RemoteBucketsMetadata

class RemoteManagerImpl(
    override val settings: RemoteSettings,
    private val converter: StringFormat
) : RemoteManager {
    private val client = OkHttpClient()
    // TODO handle situation when settings.endpoint already contains "metadata.json"
    private val metadataUrl = settings.endpoint + "metadata.json"

    override suspend fun getMetadata(): RemoteBucketsMetadata? {
        val request = Request.Builder().url(metadataUrl).build()
        val response = client.newCall(request).execute()
        val body = response.body?.string()
        if (response.code != 200) {
            return null
        }
        if (body.isNullOrEmpty()) return null

        return converter.decodeFromString<RemoteBucketsMetadata>(body)
    }

    override suspend fun getStrings(metadata: RemoteBucketsMetadata): List<RemoteBucket> {
        val buckets = mutableListOf<RemoteBucket>()

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
                val bucketInfo = result.request.tag() as RemoteBucketMetadata
                val bucket = parseBucket(result, bucketInfo)
                buckets.add(bucket)
            }
        }
        return buckets
    }

    private fun parseBucket(response: Response, info: RemoteBucketMetadata): RemoteBucket {
        val body = response.body?.string()
        if (response.code != 200) {
            throw IllegalStateException("Response code is not 200.")
        }
        if (body.isNullOrEmpty()) throw IllegalStateException("Empty response")
        val decoded: RemoteBucket = converter.decodeFromString(body)
        return decoded.copy(name = info.name, language = info.language)
    }

    private fun RemoteBucketMetadata.toRequest(url: String, language: String): Request {
        return Request.Builder().url(url + generateFilename(language)).tag(this).build()
    }

    private fun RemoteBucketMetadata.generateFilename(language: String): String {
        return "$name-$schemeVersion-$language.json"
    }
}