package org.dynodict.remote

import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.dynodict.Bucket
import org.dynodict.BucketInfo
import org.dynodict.DString

interface RemoteManager {
    suspend fun getMetadata(): org.dynodict.Metadata?
    suspend fun getStrings(info: List<BucketInfo>): Map<BucketInfo, Bucket>
}

class RemoteManagerImpl(val remoteSettings: RemoteSettings) : RemoteManager {
    val client = OkHttpClient()
    val metadataUrl = remoteSettings.baseUrl + "metadata.json"
    val translationUrl = remoteSettings.baseUrl + "login-1-ua.json"

    override suspend fun getMetadata(): org.dynodict.Metadata? {
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

        return Json.decodeFromString<org.dynodict.Metadata>(body)
    }

    private fun BucketInfo.toRequest(url: String): Request {
        return Request.Builder()
            .url(url + generateFilename())
            .build()
    }

    override suspend fun getStrings(info: List<BucketInfo>): Map<BucketInfo, Bucket> {

        val result = mutableMapOf<BucketInfo, Bucket>()
        val requests = info.map {
            it.toRequest(remoteSettings.baseUrl)
        }
        coroutineScope {
            val deferred = requests.map {
                async {
                    client.newCall(it).execute()
                }
            }

            deferred.forEach{
                val result = it.await()
                parseBucket(result)
            }

        }


//        val response = client.newCall(request).execute()
//        val body = response.body?.string()
//        if (response.code != 200) {
//            Log.d(TAG, "Error: $response")
//            return emptyMap()
//        }
//        Log.d(TAG, "Body: $body")
//        if (body.isNullOrEmpty()) return emptyMap()

//        return Json.decodeFromString<org.dynodict.Metadata>(body)
    }
    private fun parseBucket(response: Response):List<DString>{
        val body = response.body?.string()
        if (response.code != 200) {
            Log.d(TAG, "Error: $response")
            return emptyList()
        }
        Log.d(TAG, "Body: $body")
        if (body.isNullOrEmpty()) return emptyList()
//
        return Json.decodeFromString<org.dynodict.Metadata>(body)
    }

    companion object {
        const val TAG = "RemoteManagerImpl"
    }

    fun BucketInfo.generateFilename(): String {
        return "$bucketName-$schemeVersion-$locale"
    }
}

