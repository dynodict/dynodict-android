package org.dynodict.plugin.generation

import org.dynodict.plugin.remote.Bucket

class TreeInflater {
    fun generateTree(bucket: Bucket): MutableMap<String, StringModel> {
        val roots = mutableMapOf<String, StringModel>()

        bucket.translations.map { dString ->
            val splitted = dString.key.split(".")

            var parent: StringModel.Container? = null
            splitted.forEachIndexed { index, value ->
                var currentContainer =
                    if (parent == null) {
                        // roots should only be taken from Holder
                        roots[value]
                    } else {
                        parent?.children?.get(value)
                    } as StringModel.Container?

                if (currentContainer == null) {
                    currentContainer = StringModel.Container(mutableMapOf())
                        .also {
                            if (parent == null) {
                                roots[value] = it
                            } else if (index < splitted.lastIndex) {
                                parent?.children?.put(value, it)
                            } else if (index == splitted.lastIndex) {
                                parent?.children?.put(value, StringModel.Leaf(dString))
                            }
                        }
                }
                parent = currentContainer
            }
        }
        return roots
    }
}