package org.dynodict

import kotlinx.serialization.Serializable

@Serializable
data class Metadata(val buckets: List<BucketMetadata>)

