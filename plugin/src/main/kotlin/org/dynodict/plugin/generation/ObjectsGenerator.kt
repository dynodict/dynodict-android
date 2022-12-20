package org.dynodict.plugin.generation

class ObjectsGenerator(private val packageName: String) {

    private fun StringBuilder.generateModel(key: String, model: StringModel, parent: String?, level: Int) {
        if (model.children.isNotEmpty()) {
            generateContainer(parent, key, model, level)
        }
        val value = model.value
        if (value != null) {
            generateLeaf(parent, key, level)
        }
    }

    private fun StringBuilder.generateContainer(
        parent: String?, key: String, model: StringModel,
        level: Int
    ) {
        val parentClass = if (parent == null) "" else ", $parent"
        appendLine("object $key: StringKey(\"$key\"$parentClass ) {")
        model.children.forEach { generateModel(it.key, it.value, key, level + 1) }
        appendLine("}")
    }

    private fun StringBuilder.generateLeaf(parent: String?, key: String, level: Int) {
        val parentClass = if (parent == null) "" else ", $parent"
        appendLine("object $key: StringKey(\"$key\"$parentClass ) {")
        appendLine(
            "fun get(): String {" + "DynoDict.get(absolutePath)" + "}"
        )
        append("}")
    }

    private fun StringBuilder.generateHeader() {
        // package
        appendLine("package $packageName")
        appendLine()

        // imports
        appendLine("import org.dynodict")
        appendLine("import org.dynodict.model.StringKey")
        appendLine()
    }

    fun generate(roots: Map<String, StringModel>): String {
        val builder = StringBuilder()

        builder.generateHeader()

        roots.forEach {
            builder.generateModel(it.key, it.value, parent = null, level = 0)
        }
        return builder.toString()
    }
}