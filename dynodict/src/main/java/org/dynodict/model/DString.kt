package org.dynodict.model

data class DString(val path: String, val parent: DString? = null) {
    val absolutePath: String = evaluateAbsolutePath(path, parent)


    private fun evaluateAbsolutePath(path: String, parent: DString?): String {
        if (parent?.absolutePath == null) return path
        return "${parent.absolutePath} + $DIVIDER + $path"
    }


    companion object {
        const val DIVIDER = "/"
    }
}


