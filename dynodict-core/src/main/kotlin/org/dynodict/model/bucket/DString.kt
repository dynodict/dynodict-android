package org.dynodict.model.bucket

import kotlinx.serialization.Serializable

@Serializable
data class DString(
    val key: String,
    val value: String
)


