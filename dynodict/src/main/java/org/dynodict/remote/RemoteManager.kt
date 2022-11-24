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
import org.dynodict.model.DString

interface RemoteManager {
    suspend fun getMetadata(): MetadataResponse?
    suspend fun getStrings(info: List<BucketMetadata>): List<Bucket>
}

class RemoteManagerImpl(val remoteSettings: RemoteSettings) : RemoteManager {
    val client = OkHttpClient()
    val metadataUrl = remoteSettings.baseUrl + "metadata.json"
    val translationUrl = remoteSettings.baseUrl + "login-1-ua.json"

    override suspend fun getMetadata(): MetadataResponse? {
        val request = Request.Builder()
            .url(metadataUrl)
            .build()
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

    private fun BucketMetadata.toRequest(url: String): Request {
        return Request.Builder()
            .url(url + generateFilename())
            .tag(this)
            .build()
    }

    override suspend fun getStrings(info: List<BucketMetadata>): List<Bucket> {
        val parsedResponse = mutableListOf<Bucket>()
        val requests = info.map {
            it.toRequest(remoteSettings.baseUrl)
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
                val bucket = parseBucket(result,)
                parsedResponse.add(bucket)
            }
        }
        return parsedResponse
    }

    private fun parseTranslationsContainer(element: JsonElement?, parent: DString?, container: MutableList<DString>) {
        if (element is JsonObject) {
            val value = element["value"]?.jsonPrimitive?.content

            if (value == null) {
                element.entries.forEach { entry ->
                    parseTranslationsContainer(entry.value, parent = DString(entry.key), container = container)
                }
            } else {
                container.add(DString(value, parent))
            }
        }
    }

    private fun parseBucket(response: Response, info: ): Bucket {
        val body = response.body?.string()
        if (response.code != 200) {
            Log.d(TAG, "Error: $response")
            throw IllegalStateException(" ")
        }
        if (body.isNullOrEmpty()) throw IllegalStateException()
        val jsonObject = Json.parseToJsonElement(body).jsonObject

        val schemeVersion = jsonObject["schemeVersion"]?.jsonPrimitive?.int!!
        val editionVersion = jsonObject["editionVersion"]?.jsonPrimitive?.int!!
        val translations = jsonObject["translations"]
        val result = mutableListOf<DString>()

        parseTranslationsContainer(translations, parent = DString(), result)

        return Bucket(editionVersion, schemeVersion, editionVersion, result)
    }

    companion object {
        const val TAG = "RemoteManagerImpl"
    }

    fun BucketMetadata.generateFilename(): String {
        val locale = if (languages.isNotEmpty()) "-$languages[0]" else ""
        return "$name-$schemeVersion$locale.json"
    }
}

