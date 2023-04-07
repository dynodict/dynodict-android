package org.dynodict.remote.model.metadata

import kotlinx.serialization.Serializable

@Serializable
data class RemoteBucketMetadata(
    val name: String,
    val schemeVersion: Int,
    val editionVersion: Int,
    val language: String = ""
)