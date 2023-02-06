package org.dynodict.mapper

import org.dynodict.model.metadata.BucketMetadata
import org.dynodict.model.metadata.BucketsMetadata
import org.dynodict.remote.model.metadata.RemoteBucketMetadata
import org.dynodict.remote.model.metadata.RemoteBucketsMetadata

fun RemoteBucketsMetadata.toDomainMetadata(): BucketsMetadata {
    return BucketsMetadata(
        defaultLanguage = defaultLanguage,
        languages = languages,
        buckets = buckets.map { it.toDomainMetadata() })
}

fun RemoteBucketMetadata.toDomainMetadata(): BucketMetadata {
    return BucketMetadata(
        name = name,
        schemeVersion = schemeVersion,
        editionVersion = editionVersion,
        language = language
    )
}

fun BucketsMetadata.toRemoteMetadata(): RemoteBucketsMetadata {
    return RemoteBucketsMetadata(
        defaultLanguage = defaultLanguage,
        languages = languages,
        buckets = buckets.map { it.toRemoteMetadata() })
}

fun BucketMetadata.toRemoteMetadata(): RemoteBucketMetadata {
    return RemoteBucketMetadata(
        name = name,
        schemeVersion = schemeVersion,
        editionVersion = editionVersion,
        language = language
    )
}