package org.dynodict.plugin.generation

import org.dynodict.plugin.exception.IllegalTypeException
import org.dynodict.remote.model.bucket.RemoteDString
import org.dynodict.remote.model.bucket.RemoteParameter
import java.util.Locale

class ObjectsGenerator(private val packageName: String) {

    fun generate(roots: Map<String, StringModel>, customFormats: MutableSet<String>): String {
        val builder = StringBuilder()

        builder.generateHeader()

        roots.forEach {
            builder.generateModel(it.key, it.value, parent = null, level = 0, customFormats)
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
        appendLine("import org.dynodict.model.Parameter")
        appendLine()
    }

    private fun StringBuilder.generateModel(
        key: String,
        model: StringModel,
        parent: String?,
        level: Int,
        customFormats: MutableSet<String>,
    ) {

        val value = model.value

        val parentClass = if (parent == null) "" else ", $parent"
        val signature = "object $key : StringKey(\"$key\"$parentClass) {"

        appendLineWithTab(signature, level)

        if (value != null) {
            insertValueWithParam(value, level, customFormats)
        }

        model.children.let {
            if (it.isNotEmpty()) {
                if (value != null) appendLine()
                it.forEach { child -> generateModel(child.key, child.value, key, level + 1, customFormats) }
            }
        }

        appendLineWithTab("}", level)

    }

    private fun StringBuilder.insertValueWithParam(model: RemoteDString, level: Int, customFormats: MutableSet<String>) {
        if (model.params.isEmpty()) {
            appendLineWithTab("val value: String", level + 1)
            appendLineWithTab("get() = DynoDict.instance.get(this)", level + 2)
        } else {
            appendWithTab("fun value(", level + 1)
            model.params.forEachIndexed { index, parameter ->
                val suffix =
                    if (index < model.params.lastIndex) ", " else ""
                append(parameter.generateCode() + suffix)
                if (!parameter.format.isNullOrBlank()) {
                    customFormats.add(parameter.format!!)
                }
            }
            appendLine("): String {")
            val paramsString = model.params.prepareListOfParameters()
            appendLineWithTab(
                "return DynoDict.instance.get(this$paramsString)",
                level + 2
            )
            appendLineWithTab("}", tabs = level + 1)
        }
    }

    private fun List<RemoteParameter>.prepareListOfParameters(): String {
        val params = joinToString(separator = ",") { item ->
            val type = item.identifyType()

            val format = if (item.format == null) "" else ", format = \"${item.format}\""
            "Parameter.${type}Parameter(${item.key}, key = \"${item.key}\"$format)"
        }
        return if (params.isEmpty()) params else ", $params"
    }

    private val types = mapOf(
        "string" to "String",
        "int" to "Int",
        "long" to "Long",
        "float" to "Float",
        "double" to "Double"
    )

    private fun RemoteParameter.generateCode(): String {
        val evaluatedType = identifyType()
        return "$key: $evaluatedType"
    }

    private fun RemoteParameter.identifyType(): String {
        val evaluatedType = types[type.toLowerCase(Locale.getDefault())]

        if (evaluatedType == null) {
            throw IllegalTypeException("This type ($type) is not supported. Please use one of: ${types.keys}")
        }
        return evaluatedType
    }
}