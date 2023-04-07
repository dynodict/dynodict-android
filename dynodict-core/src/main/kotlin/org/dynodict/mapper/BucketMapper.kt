package org.dynodict.mapper

import org.dynodict.model.bucket.Bucket
import org.dynodict.model.bucket.DString
import org.dynodict.remote.model.bucket.RemoteBucket
import org.dynodict.remote.model.bucket.RemoteDString

fun RemoteBucket.toDomainBucket(): Bucket {
    return Bucket(
        schemeVersion = schemeVersion,
        editionVersion = editionVersion,
        name = name.orEmpty(),
        language = language.orEmpty(),
        translations = translations.map { it.toDomainDString() }
    )
}

fun RemoteDString.toDomainDString(): DString {
    return DString(key = key, value = value)
}