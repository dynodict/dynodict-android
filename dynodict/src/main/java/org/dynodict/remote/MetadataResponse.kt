package org.dynodict.remote

import kotlinx.serialization.Serializable

@Serializable
data class MetadataResponse(
    val buckets: List<BucketMetadata>)

