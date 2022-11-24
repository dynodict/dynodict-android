package org.dynodict.model

data class Bucket(
    val editionVersion: Int,
    val bucketName: String,
    val locale: String,
    val schemeVersion: Int,
    val strings: Map<DString, String>
)