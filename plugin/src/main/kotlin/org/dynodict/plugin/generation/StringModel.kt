package org.dynodict.plugin.generation

import org.dynodict.model.DString

class StringModel(val children: MutableMap<String, StringModel> = mutableMapOf(), val value: DString? = null)