package org.dynodict.model

import kotlinx.serialization.Serializable

@Serializable
data class Bucket(
    val schemeVersion: Int,
    val editionVersion: Int,
    val name: String? = null, // nullable because on BE
    val language: String? = null,
    val translations: List<DString> = emptyList(),
)