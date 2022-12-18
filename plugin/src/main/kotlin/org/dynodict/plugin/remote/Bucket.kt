package org.dynodict.plugin.remote

import kotlinx.serialization.Serializable

@Serializable
data class Bucket(
    val schemeVersion: Int,
    val editionVersion: Int,
    val name: String? = null, // nullable because on BE
    val language: String? = null,
    // TODO rename to strings
    val translations: List<DString> = emptyList(),
)