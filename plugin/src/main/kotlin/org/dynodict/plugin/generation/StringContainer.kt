package org.dynodict.plugin.generation

import org.dynodict.plugin.remote.DString

//sealed class StringModel {
//    data class Container(val children: MutableMap<String, StringModel>) : StringModel()
//    data class Leaf(val value: DString) : StringModel()
//}

class StringModel(val children: MutableMap<String, StringModel> = mutableMapOf(), val value: DString? = null)