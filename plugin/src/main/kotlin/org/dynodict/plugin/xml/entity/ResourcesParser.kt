package org.dynodict.plugin.xml.entity

import groovy.util.Node
import groovy.util.NodeList
import groovy.xml.XmlParser
import org.dynodict.remote.model.bucket.RemoteDString
import org.dynodict.remote.model.bucket.RemoteParameter
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern

class ResourcesParser {

    fun parse(data: String): List<RemoteDString> {
        val result = mutableListOf<RemoteDString>()
        val resources = XmlParser().parseText(data)
        resources.children().map { it as Node }.forEach {
            val translatable = it.attribute("translatable")
            val key = prepareKey(it.attribute("name").toString())
            val (value, params) = parseValue((it.value() as NodeList)[0].toString())
            if (translatable == "false") {
                return@forEach
            } else {
                val string = RemoteDString(key, value, params)
                result.add(string)
            }
        }
        return result
    }

    private fun parseValue(value: String): Pair<String, List<RemoteParameter>> {
        val pattern: Pattern = Pattern.compile(REGEX)
        val matcher: Matcher = pattern.matcher(value)
        val parameters = mutableListOf<RemoteParameter>()

        val formatSpecifiers: MutableList<String> = ArrayList()

        val stringBuilder = StringBuilder()
        String.format("sdf")
        var index = 0
        var start = 0
        var end = 0
        while (matcher.find()) {
            start = matcher.start()
            if (end == 0) {
                stringBuilder.append(value.substring(0, start))
            } else {
                stringBuilder.append(value.substring(end, start))
            }
            end = matcher.end()
            stringBuilder.append("{${index++}}")
            formatSpecifiers.add(value.substring(start, end))
        }
        stringBuilder.append(value.substring(end))
        // %s %d %f
        // %.8f
        // %15.8f
        // %1$s
        // %07d
        // value.format(1, 2)
        println("Format specifiers: $formatSpecifiers")
        return stringBuilder.toString() to parameters
    }

    private fun prepareKey(name: String): String {
        return name.split("_").joinToString(".") { it.capitalize(Locale.ROOT) }
    }

    companion object {

        const val TAG = "ResourcesParser"
        const val REGEX = "%([0-9\$]*[-+#, 0(]*\\d*\\.?\\d*[bBhHsScCdoxXeEfgGaAtT%])"

    }
}
