package org.dynodict.plugin.generation

data class StringContainer(val key: String, val children: MutableList<StringContainer>, val value: String? = null)