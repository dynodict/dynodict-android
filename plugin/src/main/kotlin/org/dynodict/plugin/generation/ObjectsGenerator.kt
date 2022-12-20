package org.dynodict.plugin.generation

class ObjectsGenerator(private val packageName: String) {

    fun generate(roots: Map<String, StringModel>): String {
        val builder = StringBuilder()

        builder.generateHeader()

        roots.forEach {
            builder.generateModel(it.key, it.value, parent = null, level = 0)
        }
        return builder.toString()
    }

    private fun StringBuilder.generateHeader() {
        // package
        appendLine("package $packageName")
        appendLine()

        // imports
        appendLine("import org.dynodict.DynoDict")
        appendLine("import org.dynodict.model.StringKey")
        appendLine("import org.dynodict.model.Key")
        appendLine()
    }

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
        parent: String?, key: String, model: StringModel, level: Int
    ) {
        val parentClass = if (parent == null) "" else ", $parent"
        appendLineWithTab("object $key : StringKey(\"$key\"$parentClass ) {", level)
        model.children.forEach { generateModel(it.key, it.value, key, level + 1) }
        appendLineWithTab("}", level)
    }


    private fun StringBuilder.generateLeaf(parent: String?, key: String, level: Int) {
        val parentClass = if (parent == null) "" else ", $parent"
        appendLineWithTab("object $key : StringKey(\"$key\"$parentClass ) {", level)
        appendLineWithTab("fun get(): String {", level + 1)
        appendLineWithTab("return DynoDict.instance.get(Key(absolutePath))", level + 2)
        appendLineWithTab("}", tabs = level + 1)
        appendLineWithTab("}", level)
    }

    private fun StringBuilder.appendLineWithTab(value: String, tabs: Int) {
        repeat(tabs) { append(TAB) }
        appendLine(value)
    }

    companion object {
        private const val TAB = "    "
    }
}