package org.dynodict.model

import kotlinx.serialization.Serializable

@Serializable
data class RemoteParameter(val format: String? = null, val type: String, val key: String)