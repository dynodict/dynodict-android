package org.dynodict.storage

import org.dynodict.model.Bucket
import org.dynodict.model.metadata.BucketsMetadata

interface BucketsStorage {
    suspend fun store(bucket: Bucket)
    suspend fun get(name: String, language: String, schemeVersion: Int): Bucket?
}

interface MetadataStorage {
    suspend fun store(metadata: BucketsMetadata)
    suspend fun get(): BucketsMetadata?
}

interface ObservableStorage : BucketsStorage {
    var listener: (() -> Unit)?
}

//class InMemoryObservableStorage() : ObservableStorage {
//    override var listener: (() -> Unit)? = null
//    override var value: Map<DString, String> by Delegates.observable(mutableMapOf()) { changed, old, new ->
//        if (old != new)
//            listener?.invoke()
//    }
//}
