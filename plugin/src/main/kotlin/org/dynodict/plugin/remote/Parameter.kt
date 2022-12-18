package org.dynodict.plugin.remote

import kotlinx.serialization.Serializable

@Serializable
data class Parameter(val format: String, val type: String, val key: String)