package org.dynodict.remote

import org.dynodict.remote.model.bucket.RemoteBucket
import org.dynodict.remote.model.metadata.RemoteBucketsMetadata

interface RemoteManager {
    val settings: RemoteSettings

    suspend fun getMetadata(): RemoteBucketsMetadata?
    suspend fun getStrings(metadata: RemoteBucketsMetadata): List<RemoteBucket>
}
