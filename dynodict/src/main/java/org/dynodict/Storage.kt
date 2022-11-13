package org.dynodict

import kotlin.properties.Delegates

interface Storage {
    var value: Map<Translation, String>
}

interface ObservableStorage : Storage {
    var listener: (() -> Unit)?
}

class InMemoryObservableStorage() : ObservableStorage {
    override var listener: (() -> Unit)? = null
    override var value: Map<Translation, String> by Delegates.observable(mutableMapOf()) { changed, old, new ->
        if (old != new)
            listener?.invoke()
    }
}
