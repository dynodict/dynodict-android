package org.dynodict.model

open class StringKey(val key: String, val parent: StringKey? = null) {
    val absolutePath: String = if (parent?.absolutePath == null) {
        key
    } else {
        parent.absolutePath + DIVIDER + key
    }

    companion object {
        const val DIVIDER = "."
    }
}