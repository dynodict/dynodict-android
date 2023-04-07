package org.dynodict.model.bucket

import kotlinx.serialization.Serializable

@Serializable
data class Bucket(
    val schemeVersion: Int,
    val editionVersion: Int,
    val name: String,
    val language: String,
    val translations: List<DString> = emptyList(),
)