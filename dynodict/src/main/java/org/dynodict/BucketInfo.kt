package org.dynodict

data class BucketInfo(
    val editionVersion: Int,
    val bucketName: String,
    val locale: String,
    val schemeVersion: Int
)