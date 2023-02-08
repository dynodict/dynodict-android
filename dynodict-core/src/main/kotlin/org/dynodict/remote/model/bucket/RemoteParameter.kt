package org.dynodict.remote.model.bucket

import kotlinx.serialization.Serializable

@Serializable
data class RemoteParameter(val type: String, val key: String, val format: String? = null)