package org.dynodict.remote.model.metadata

import kotlinx.serialization.Serializable

@Serializable
data class RemoteBucketsMetadata(
    val defaultLanguage: String,
    val languages: List<String>,
    val buckets: List<RemoteBucketMetadata>
)

