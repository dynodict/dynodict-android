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
import org.dynodict.storage.generateBucketName
import java.io.File

class RemoteManagerImpl(
    override val settings: RemoteSettings,
    private val converter: StringFormat,
) : RemoteManager {

    private val client = OkHttpClient()

    private val metadataUrl by lazy {
        if (settings.endpoint.contains("metadata.json")) {
            settings.endpoint
        } else {
            settings.endpoint + "metadata.json"
        }
    }

    private fun determineTheType(urlOrPath: String): SourceType {
        val webUrlPattern = Regex("^https?://.*")

        return when {
            webUrlPattern.matches(urlOrPath) -> SourceType.WEB_URL
            else -> SourceType.LOCAL_FILE
        }
    }

    private fun getMetadataFromWeb(): RemoteBucketsMetadata? {
        val request = Request.Builder().url(metadataUrl).build()
        val response = client.newCall(request).execute()
        val body = response.body?.string()
        if (response.code != 200) {
            return null
        }
        if (body.isNullOrEmpty()) return null

        return converter.decodeFromString<RemoteBucketsMetadata>(body)
    }

    private fun getMetadataFromFile(): RemoteBucketsMetadata? {
        val body = File(metadataUrl).readText()
        if (body.isEmpty()) return null

        return converter.decodeFromString<RemoteBucketsMetadata>(body)
    }

    override suspend fun getMetadata(): RemoteBucketsMetadata? {
        return when (determineTheType(settings.endpoint)) {
            SourceType.WEB_URL -> getMetadataFromWeb()
            SourceType.LOCAL_FILE -> getMetadataFromFile()
        }
    }

    override suspend fun getStrings(metadata: RemoteBucketsMetadata): List<RemoteBucket> {
        val buckets = mutableListOf<RemoteBucket>()

        when (determineTheType(settings.endpoint)) {
            SourceType.WEB_URL -> {
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

            SourceType.LOCAL_FILE -> {
                val requests = metadata.buckets.flatMap { bucket ->
                    metadata.languages.map { language ->
                        settings.endpoint + bucket.generateFilename(language)
                    }
                }

                coroutineScope {
                    val deferred = requests.map {
                        async {
                            File(it).readText()
                        }
                    }

                    deferred.forEach {
                        val result = it.await()
                        val decoded: RemoteBucket = converter.decodeFromString(result)
                        buckets.add(decoded)
                    }
                }
                return buckets

            }
        }
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
        return generateBucketName(name, language, schemeVersion)
    }

    private enum class SourceType {
        WEB_URL,
        LOCAL_FILE
    }
}