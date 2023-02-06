package org.dynodict.remote.model.bucket

import kotlinx.serialization.Serializable

@Serializable
data class RemoteBucket(
    val schemeVersion: Int,
    val editionVersion: Int,
    val name: String? = null, // nullable because on BE
    val language: String? = null,
    val translations: List<RemoteDString> = emptyList(),
)