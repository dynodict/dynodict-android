package org.dynodict.model.metadata

import kotlinx.serialization.Serializable

@Serializable
data class BucketsMetadata(
    val defaultLanguage: String,
    val languages: List<String>,
    val buckets: List<BucketMetadata>
)

