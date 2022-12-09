package org.dynodict.remote

import org.dynodict.model.Bucket
import org.dynodict.model.metadata.BucketsMetadata

interface RemoteManager {
    val settings: RemoteSettings

    suspend fun getMetadata(): BucketsMetadata?
    suspend fun getStrings(metadata: BucketsMetadata): List<Bucket>
}
