package org.dynodict.model

data class Key(val path: String, val params: List<Parameter> = emptyList())