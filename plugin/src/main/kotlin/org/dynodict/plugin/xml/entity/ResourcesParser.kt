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

        val formatSpecifiers: MutableList<FormatSpecifier> = ArrayList()

        val stringBuilder = StringBuilder()

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
            val specifier = value.substring(start, end)
            // parse parameter's position in specifier
            val position = specifier
                .substring(1)
                .split("$")
                .takeIf { it.size > 1 }
                ?.get(0)?.toInt()

            val formatSpecifier = parseParameter(position, specifier)
            formatSpecifiers.add(formatSpecifier)
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

    private fun parseParameter(
        position: Int?,
        specifier: String,
    ): FormatSpecifier {
        val formatSpecifier = FormatSpecifier(
            position = position,
            fullDescription = specifier,
            migratedFormat = specifier.replace(Regex("%[0-9]\\\$"), "%"),
            type = when {
                specifier.contains("f") -> {
                    val precision = specifier
                        .substringAfter(".")
                        .substringBefore("f")
                        .toIntOrNull() ?: 0
                    val width = specifier
                        .substringAfter("%")
                        .substringBefore(".")
                        .toIntOrNull() ?: 0
                    FormatType.FloatType(precision, width)
                }

                specifier.contains("d") -> {
                    val width = specifier
                        .substringAfter("%")
                        .substringBefore("d")
                        .toIntOrNull() ?: 0
                    FormatType.Integer(width)
                }

                specifier.contains("s") -> {
                    val width = specifier
                        .substringAfter("%")
                        .substringBefore("s")
                        .toIntOrNull() ?: 0
                    FormatType.StringType(width)
                }

                specifier.contains("c") -> {
                    val width = specifier
                        .substringAfter("%")
                        .substringBefore("c")
                        .toIntOrNull() ?: 0
                    FormatType.CharType(width)
                }

                specifier.contains("b") -> {
                    val width = specifier
                        .substringAfter("%")
                        .substringBefore("b")
                        .toIntOrNull() ?: 0
                    FormatType.ByteType(width)
                }

                specifier.contains("h") -> {
                    val width = specifier
                        .substringAfter("%")
                        .substringBefore("h")
                        .toIntOrNull() ?: 0
                    FormatType.ByteType(width)
                }

                specifier.contains("o") -> {
                    val width = specifier
                        .substringAfter("%")
                        .substringBefore("o")
                        .toIntOrNull() ?: 0
                    FormatType.ByteType(width)
                }

                specifier.contains("x") -> {
                    val width = specifier
                        .substringAfter("%")
                        .substringBefore("x")
                        .toIntOrNull() ?: 0
                    FormatType.ByteType(width)
                }

                specifier.contains("X") -> {
                    val width = specifier
                        .substringAfter("%")
                        .substringBefore("X")
                        .toIntOrNull() ?: 0
                    FormatType.ByteType(width)
                }

                specifier.contains("e") -> {
                    val width = specifier
                        .substringAfter("%")
                        .substringBefore("e")
                        .toIntOrNull() ?: 0
                    FormatType.ByteType(width)
                }

                else -> FormatType.Undefined
            }

        )
        return formatSpecifier
    }

    private fun prepareKey(name: String): String {
        return name.split("_").joinToString(".") { it.capitalize(Locale.ROOT) }
    }

    companion object {

        const val TAG = "ResourcesParser"
        const val REGEX = "%([0-9\$]*[-+#, 0(]*\\d*\\.?\\d*[bBhHsScCdoxXeEfgGaAtT%])"

    }
}

private data class FormatSpecifier(
    val position: Int?,
    val fullDescription: String,
    val type: FormatType,
    val migratedFormat: String, // it is the format without unneeded data. %3$s -> %s
)

sealed class FormatType {
    data class FloatType(val precision: Int, val width: Int) : FormatType()
    data class DoubleType(val precision: Int, val width: Int) : FormatType()
    data class StringType(val width: Int) : FormatType()
    data class Integer(val width: Int) : FormatType()
    data class CharType(val width: Int) : FormatType()
    data class ByteType(val width: Int) : FormatType()
    object Undefined : FormatType()
}

