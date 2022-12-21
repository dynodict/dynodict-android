package org.dynodict.plugin.remote

import kotlinx.serialization.Serializable

@Serializable
data class Parameter(val format: String? = null, val type: String, val key: String)