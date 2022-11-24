package org.dynodict.model

import kotlinx.serialization.Serializable

@Serializable
data class Bucket(
    val editionVersion: Int,
    val bucketName: String?,
    val language: String?,
    val schemeVersion: Int,
    val translations: List<DString>
//    val strings: Map<DString, String>
)