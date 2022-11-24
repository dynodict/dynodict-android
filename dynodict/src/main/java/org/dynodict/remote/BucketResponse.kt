package org.dynodict.remote

import org.dynodict.model.DString

data class BucketResponse(
    val schemeVersion: Int,
    val editionVersion: Int,
    val translations: List<DString>
)