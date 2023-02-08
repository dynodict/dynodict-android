package org.dynodict.plugin.generation

import org.dynodict.remote.model.bucket.RemoteDString

class StringModel(val children: MutableMap<String, StringModel> = mutableMapOf(), val value: RemoteDString? = null)