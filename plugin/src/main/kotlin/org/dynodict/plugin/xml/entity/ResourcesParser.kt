package org.dynodict.plugin.xml.entity

import groovy.util.Node
import groovy.util.NodeList
import groovy.xml.XmlParser
import org.dynodict.remote.model.bucket.RemoteDString
import org.dynodict.remote.model.bucket.RemoteParameter
import java.io.File
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern

class ResourcesParser {

    fun parse(path: String): List<RemoteDString> {
        val file = File(path)
        val result = mutableListOf<RemoteDString>()
        println("file before parsing started")
        val resources = XmlParser().parse(file)
        resources.children().map { it as Node }.forEach {
            println("Value: ${it.attribute("name")} -> ${it.value()}")
            val translatable = it.attribute("translatable")
            val key = prepareKey(it.attribute("name").toString())
            val (value, params) = parseValue((it.value() as NodeList)[0].toString())
            if (translatable == "false") {
                return@forEach
            } else {
                val string = RemoteDString(
                    key, value, params.takeIf { it.isNotEmpty() }.orEmpty()
                )
                result.add(string)
                println("String: $string")
            }
        }
        return result
    }

    private fun parseValue(value: String): Pair<String, List<RemoteParameter>> {
        println("Value: $value")
        var valueChanged = value
        val pattern: Pattern = Pattern.compile(REGEX)
        val matcher: Matcher = pattern.matcher(value)
        val parameters = mutableListOf<RemoteParameter>()

        var parameterIndex = 0
        val formatSpecifiers: MutableList<String> = ArrayList()

        while (matcher.find()) {
            println("Found: ${matcher.group(0)}")
//            matcher.replaceFirst("{${parameterIndex++}}")
            parameters.add(RemoteParameter("string", "param$parameterIndex", matcher.group(0)))
            formatSpecifiers.add(matcher.group(0))
        }

        for (specifier in formatSpecifiers) {
            println("Format Specifier: $specifier")
        }
        // %s %d %f
        // %.8f
        // %15.8f
        // %1$s
        // %07d
        // value.format(1, 2)
        return valueChanged to parameters
    }

    private fun prepareKey(name: String): String {
        return name.split("_").joinToString(".") { it.capitalize(Locale.ROOT) }
    }

    companion object {

        const val TAG = "ResourcesParser"
        const val REGEX = "%([0-9\$]*[-+#, 0(]*\\d*\\.?\\d*[bBhHsScCdoxXeEfgGaAtT%])"

    }
}
