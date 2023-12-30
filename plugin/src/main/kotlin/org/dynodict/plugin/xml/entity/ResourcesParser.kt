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

        val stringBuilder = StringBuilder(value)

        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            val specifier = value.substring(start, end)

            val formatSpecifier = parseParameter(specifier)
            formatSpecifiers.add(formatSpecifier)
        }
        // this is the list of already used position when position is passed via argument: %3$s
        val reservedPositions = formatSpecifiers
            .filter { it.position != null }
            .map { it.position }

        // convert specifier to parameter
        // name if according to position
        var index = 0
        parameters.addAll(
            formatSpecifiers.map { formatSpecifier ->
                val position = formatSpecifier.position
                val name = if (position != null) {
                    "param$position"
                } else {
                    while (reservedPositions.contains(index)) {
                        index++
                    }
                    "param$index".also {
                        index++
                    }
                }
                RemoteParameter(
                    key = name,
                    type = formatSpecifier.type.jsonType,
                    format = if (formatSpecifier.isCustom) formatSpecifier.migratedFormat else null,
                ).also {
                    val specifier = formatSpecifier.fullDescription
                    val foundIndex = stringBuilder.indexOf(specifier)
                    stringBuilder.replace(foundIndex, foundIndex + specifier.length, "{$name}")
                }
            }
        )
        return stringBuilder.toString() to parameters
    }

    private fun parseParameter(specifier: String): FormatSpecifier {

        // parse parameter's position in specifier
        val position = specifier
            .substring(1)
            .split("$")
            .takeIf { it.size > 1 }
            ?.get(0)?.toInt()
        return FormatSpecifier(
            position = position,
            fullDescription = specifier,
            migratedFormat = specifier.replace(Regex("%[0-9]\\\$"), "%"),
            type = getFormatType(specifier)
        )
    }

    private fun getFormatType(specifier: String): FormatType {
        fun width(char: Char): Int = specifier
            .substringAfter("%")
            .substringBefore(char)
            .toIntOrNull() ?: 0

        return when {
            specifier.contains("f") -> {
                if (specifier.contains(".")) {
                    val precision = specifier
                        .substringAfter(".")
                        .substringBefore("f")
                        .toIntOrNull() ?: 0
                    FormatType.FloatType(precision, width('.'))
                } else {
                    FormatType.FloatType(0, width('f'))
                }
            }

            specifier.contains("d") -> FormatType.Integer(width('d'))
            specifier.contains("s") -> FormatType.StringType(width('s'))
            specifier.contains("c") -> FormatType.CharType(width('c'))
            specifier.contains("o") -> FormatType.Integer(width('o'))
            specifier.contains("x") -> FormatType.Integer(width('x'))
            specifier.contains("X") -> FormatType.Integer(width('X'))

            else -> FormatType.Undefined
        }
    }

    private fun prepareKey(name: String): String {
        return name.split("_").joinToString(".") { it.capitalize(Locale.ROOT) }
    }

    companion object {

        const val REGEX = "%([0-9\$]*[-+#, 0(]*\\d*\\.?\\d*[bBhHsScCdoxXeEfgGaAtT%])"
    }
}

private data class FormatSpecifier(
    val position: Int?,
    val fullDescription: String,
    val type: FormatType,
    val migratedFormat: String, // it is the format without unneeded data. %3$s -> %s
)

private val defaultSpecifiers = listOf("%s", "%d", "%f", "%c")

private val FormatSpecifier.isCustom: Boolean
    get() {
        return migratedFormat !in defaultSpecifiers
    }

sealed class FormatType {

    data class FloatType(val precision: Int, val width: Int) : FormatType()
    data class StringType(val width: Int) : FormatType()
    data class Integer(val width: Int) : FormatType()
    data class CharType(val width: Int) : FormatType()
    object Undefined : FormatType()
}

/** It is the type which is used in json */
private val FormatType.jsonType: String
    get() = when (this) {
        is FormatType.FloatType -> "float"
        is FormatType.StringType -> "string"
        is FormatType.Integer -> "integer"
        is FormatType.CharType -> "string"
        is FormatType.Undefined -> "string"
    }

