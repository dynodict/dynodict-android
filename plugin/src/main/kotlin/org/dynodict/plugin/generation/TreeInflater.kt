package org.dynodict.plugin.generation

import org.dynodict.remote.model.bucket.RemoteBucket


class TreeInflater {

    fun generateTree(bucket: RemoteBucket): MutableMap<String, StringModel> {
        val roots = mutableMapOf<String, StringModel>()

        bucket.translations.map { dString ->
            println("dString: ${dString.key}")
            val splitted = dString.key.split(".")

            var parent: StringModel? = null
            splitted.forEachIndexed { index, value ->
                var currentContainer =
                    if (parent == null) {
                        // roots should only be taken from Holder
                        roots[value]
                    } else {
                        parent?.children?.get(value)
                    }

                println("$value -> currentContainer: ${currentContainer != null}")
                if (currentContainer == null) {
                    currentContainer = StringModel()
                        .also {
                            if (parent == null) {
                                roots[value] = it
                            } else if (index < splitted.lastIndex) {
                                parent?.children?.put(value, it)
                            } else if (index == splitted.lastIndex) {
                                parent?.children?.put(value, StringModel(value = dString))
                            }
                        }
                } else {
                    if (index == splitted.lastIndex) {
                        parent?.children?.put(value, currentContainer.copy(value =  dString))
                        println("value: $value -> dString: $dString")
                    }
                }
                parent = currentContainer
            }
//            println("Roots: $roots")
        }
        return roots
    }
}