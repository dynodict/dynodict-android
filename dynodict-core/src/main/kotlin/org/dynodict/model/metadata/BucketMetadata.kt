package org.dynodict.model.metadata

import kotlinx.serialization.Serializable

@Serializable
data class BucketMetadata(
    val name: String,
    val schemeVersion: Int,
    val editionVersion: Int,
    val language: String = "",
)