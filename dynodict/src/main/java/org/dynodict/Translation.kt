package org.dynodict

interface Translation {
    val absolutePath: String

    val value: String

    companion object {
        const val DIVIDER = "/"
    }
}

interface TranslationBucket : Translation {
    val translations: List<Translation>
}