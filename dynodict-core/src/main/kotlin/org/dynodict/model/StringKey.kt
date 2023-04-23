package org.dynodict.model

open class StringKey(val key: String, val parent: StringKey? = null) {
    override fun equals(other: Any?): Boolean {
        if (other !is StringKey) return false
        return key == other.key && parent?.equals(other.parent) == true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + (parent?.hashCode() ?: 0)
        return result
    }
}