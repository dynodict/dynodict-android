package org.dynodict.plugin.generation

import org.dynodict.plugin.exception.IllegalTypeException
import org.dynodict.plugin.remote.DString
import org.dynodict.plugin.remote.Parameter
import java.util.*

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
        appendLine("import org.dynodict.model.Parameter")
        appendLine()
    }

    private fun StringBuilder.generateModel(
        key: String,
        model: StringModel,
        parent: String?,
        level: Int
    ) {

        if (model.children.isNotEmpty()) {
            generateContainer(parent, key, model, level)
        }
        val value = model.value
        if (value != null) {
            generateLeaf(parent, key, value, level)
        }
    }

    private fun StringBuilder.generateContainer(
        parent: String?, key: String, model: StringModel, level: Int
    ) {
        val parentClass = if (parent == null) "" else ", $parent"
        appendLineWithTab("object $key : StringKey(\"$key\"$parentClass) {", level)
        model.children.forEach { generateModel(it.key, it.value, key, level + 1) }
        appendLineWithTab("}", level)
    }

    private fun StringBuilder.generateLeaf(
        parent: String?,
        key: String,
        model: DString,
        level: Int
    ) {
        val parentClass = if (parent == null) "" else ", $parent"
        appendLineWithTab("object $key : StringKey(\"$key\"$parentClass) {", level)
        // ------------------------------------------------------------------------------
        appendWithTab("fun get(", level + 1)
        model.params.forEachIndexed { index, parameter ->
            val suffix =
                if (index < model.params.lastIndex) ", " else ""
            append(parameter.generateCode() + suffix)
        }
        appendLine("): String {")
        val paramsString = model.params.prepareListOfParameters()
        appendLineWithTab(
            "return DynoDict.instance.get(Key(absolutePath, params = $paramsString))",
            level + 2)
        appendLineWithTab("}", tabs = level + 1)
        // ------------------------------------------------------------------------------
        appendLineWithTab("}", level)
    }

    private fun List<Parameter>.prepareListOfParameters(): String {
        val params = joinToString(",") { item ->
            val type = item.identifyType()

            val format = if (item.format == null) "" else ", format = \"${item.format}\""
            "Parameter.${type}Parameter(${item.key}$format)"
        }
        return "listOf($params)"
    }

    private val types = mapOf(
        "string" to "String",
        "int" to "Int",
        "long" to "Long",
        "float" to "Float",
        "double" to "Double")

    private fun Parameter.generateCode(): String {
        val evaluatedType = identifyType()
        return "$key: $evaluatedType"
    }

    private fun Parameter.identifyType(): String {
        val evaluatedType = types[type.toLowerCase(Locale.getDefault())]

        if (evaluatedType == null) {
            throw IllegalTypeException("This type ($type) is not supported. Please use one of: ${types.keys}")
        }
        return evaluatedType
    }

    private fun StringBuilder.appendLineWithTab(value: String, tabs: Int) {
        repeat(tabs) { append(TAB) }
        appendLine(value)
    }

    private fun StringBuilder.appendWithTab(value: String, tabs: Int) {
        repeat(tabs) { append(TAB) }
        append(value)
    }

    companion object {
        private const val TAB = "    "
    }
}