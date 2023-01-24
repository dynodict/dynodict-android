package org.dynodict.storage.defaults

import java.io.InputStream

interface DefaultDataProvider {
    fun open(filename: String): InputStream
}