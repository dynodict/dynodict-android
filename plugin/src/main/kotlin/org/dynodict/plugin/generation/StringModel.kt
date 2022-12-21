package org.dynodict.plugin.generation

import org.dynodict.plugin.remote.DString

class StringModel(val children: MutableMap<String, StringModel> = mutableMapOf(), val value: DString? = null)