package org.dynodict

import org.dynodict.model.DString
import kotlin.properties.Delegates

interface Storage {
    var value: Map<DString, String>
}

interface ObservableStorage : Storage {
    var listener: (() -> Unit)?
}

class InMemoryObservableStorage() : ObservableStorage {
    override var listener: (() -> Unit)? = null
    override var value: Map<DString, String> by Delegates.observable(mutableMapOf()) { changed, old, new ->
        if (old != new)
            listener?.invoke()
    }
}
