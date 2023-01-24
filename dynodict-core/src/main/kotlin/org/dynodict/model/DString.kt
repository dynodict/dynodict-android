package org.dynodict.model

import kotlinx.serialization.Serializable

@Serializable
data class DString(val key: String, val value: String, val params: List<RemoteParameter> = emptyList())


