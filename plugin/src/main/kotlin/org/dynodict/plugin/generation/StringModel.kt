package org.dynodict.plugin.generation

import kotlinx.serialization.Serializable
import org.dynodict.remote.model.bucket.RemoteDString

@Serializable
data class StringModel(val children: MutableMap<String, StringModel> = mutableMapOf(), val value: RemoteDString? = null)