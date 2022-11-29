package org.dynodict.model

import kotlinx.serialization.Serializable

@Serializable
data class Bucket(
    val editionVersion: Int,
    val name: String? = null,
    val language: String? = null,
    val schemeVersion: Int,
    val translations: List<DString>
)