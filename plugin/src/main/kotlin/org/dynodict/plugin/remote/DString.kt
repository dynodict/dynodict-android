package org.dynodict.plugin.remote

import kotlinx.serialization.Serializable

@Serializable
data class DString(val key: String, val value: String, val params: List<Parameter>)


