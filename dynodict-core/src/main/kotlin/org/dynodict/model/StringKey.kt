package org.dynodict.model

data class StringKey(val key: String, val parent: StringKey? = null)