package org.dynodict

interface Storage {
    var value: List<Translation>
}

interface ObservableStorage : Storage {
    var listener: (() -> Unit)?
}