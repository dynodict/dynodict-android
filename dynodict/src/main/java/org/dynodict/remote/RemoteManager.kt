package org.dynodict.remote

import android.util.Log
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import org.dynodict.Bucket
import org.dynodict.BucketInfo

interface RemoteManager {
    suspend fun getMetadata(): org.dynodict.Metadata?
    suspend fun getStrings(info: List<BucketInfo>): Map<BucketInfo, Bucket>
}

class RemoteManagerImpl(val remoteSettings: RemoteSettings) : RemoteManager {
    val client = OkHttpClient()
    val metadataUrl = remoteSettings.baseUrl + "metadata.json"
    val translation = remoteSettings.baseUrl + "login-1-ua.json"

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

    override suspend fun getStrings(info: List<BucketInfo>): Map<BucketInfo, Bucket> {
        return emptyMap()
    }

    companion object {
        const val TAG = "RemoteManagerImpl"
    }

}

