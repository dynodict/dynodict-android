package org.dynodict.remote.model.bucket

import kotlinx.serialization.Serializable

@Serializable
data class RemoteDString(
    val key: String,
    val value: String,
    val params: List<RemoteParameter> = emptyList()
)


