package org.dynodict.model

open class StringKey(val key: String, val parent: StringKey? = null) {

    private val absolutePath = if (parent == null) key else "$parent.$key"
    private val hash = absolutePath.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other !is StringKey) return false
        return absolutePath == other.absolutePath
    }

    override fun hashCode() = hash

    override fun toString(): String {
        if (parent == null) return key
        return "$parent.$key"
    }
}
