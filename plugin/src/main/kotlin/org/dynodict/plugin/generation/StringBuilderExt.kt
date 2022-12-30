package org.dynodict.plugin.generation


object StringBuilderExt {
    const val TAG = "    "
}

fun StringBuilder.appendLineWithTab(value: String, tabs: Int) {
    repeat(tabs) { append(StringBuilderExt.TAG) }
    appendLine(value)
}

fun StringBuilder.appendWithTab(value: String, tabs: Int) {
    repeat(tabs) { append(StringBuilderExt.TAG) }
    append(value)
}
