package org.dynodict

interface DString {
    val absolutePath: String

    val value: String

    companion object {
        const val DIVIDER = "/"
    }
}

