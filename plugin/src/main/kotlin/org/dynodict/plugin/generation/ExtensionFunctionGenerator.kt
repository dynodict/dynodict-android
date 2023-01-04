package org.dynodict.plugin.generation

/**
 * @param packageName is used to generate package "com.example"
 * as the first line of the file
 * @param paramsThreshold - quantity to decide which type of function to generate. If amount is less than {paramsThreshold}
 * the function will be generated as named function i.e. Formatters will be passed separately using names
 * If the amount is bigger - function with map will be generated to prevent inconvenient function with too many parameters
 */
class ExtensionFunctionGenerator(
    private val packageName: String,
    private val paramsThreshold: Int = DEFAULT_PARAMS_THRESHOLD
) {
    fun generate(formats: List<String>): String {

        // no need to generate anything since formatters
        if (formats.isEmpty()) return ""

        val builder = StringBuilder()

        with(builder) {
            appendLine("package $packageName")
            appendLine()

            appendLine("import org.dynodict.formatter.DynoDictFormatter")
            appendLine("import org.dynodict.DynoDict")
            appendLine()

            append("fun DynoDict.Companion.registerFormatters(")
            if (formats.size <= paramsThreshold) {
                generateNamedFormatters(formats)
            } else {
                generateFormattersAsMap(formats)
            }

        }
        return builder.toString()
    }

    private fun StringBuilder.generateNamedFormatters(formats: List<String>) {
        appendLine()
        val formatsNormalized = formats.associateWith {
            it.trim().replace(".", "").replace("_", "")
        }

        formatsNormalized.values.forEachIndexed { index, item ->
            appendWithTab("${item}Formatter: DynoDictFormatter<*>", tabs = 1)
            appendLine(if (index == formats.lastIndex) "\n) {" else ",")
        }
        appendLineWithTab("with(DynoDict.instance) {", tabs = 1)
        formatsNormalized.forEach {
            appendLineWithTab("registerFormatter(\"${it.key}\", ${it.value}Formatter)", tabs = 2)
        }
        appendLineWithTab("}", tabs = 1)
        appendLine("}")
    }

    private fun StringBuilder.generateFormattersAsMap(formats: List<String>) {
        appendLine("formats: Map<String, DynoDictFormatter<*>>) {")
        val names = formats.joinToString("\", \"", prefix = "\"", postfix = "\"")
        appendLineWithTab("val list = listOf($names)", tabs = 1)
        appendLineWithTab(
            "val result = list - formats.keys", tabs = 1
        )
        appendLineWithTab("if (result.isNotEmpty()) {", tabs = 1)
        appendLineWithTab(
            "throw IllegalStateException(\"Not all formatters passed. Please also add remaining: \$result\")", tabs = 2
        )
        appendLineWithTab("}", tabs = 1)
        appendLine("}")
    }

    companion object {
        const val DEFAULT_PARAMS_THRESHOLD = 8
    }
}