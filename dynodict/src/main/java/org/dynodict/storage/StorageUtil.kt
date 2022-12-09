package org.dynodict.storage

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun generateBucketName(name: String, language: String, schemeVersion: Int): String {
    // login_1_ua.json
    return name + "_$schemeVersion" + "_$language.json"
}

fun InputStream.copyAndClose(output: File) {
    use {
        FileOutputStream(output).use { outputStream ->
            try {
                this.copyTo(outputStream, 1024)
            } finally {
                outputStream.flush()
            }
        }
    }
}

fun createFileIfNeeded(file: File): File {
    if (!file.exists()) {
        file.createNewFile()
    }
    return file
}