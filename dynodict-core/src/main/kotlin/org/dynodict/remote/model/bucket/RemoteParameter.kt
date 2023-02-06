package org.dynodict.remote.model.bucket

import kotlinx.serialization.Serializable

@Serializable
data class RemoteParameter(val format: String? = null, val type: String, val key: String)