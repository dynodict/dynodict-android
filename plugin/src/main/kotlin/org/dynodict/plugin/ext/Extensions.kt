package org.dynodict.plugin.ext

fun generateBucketName(name: String, language: String, schemeVersion: Int): String {
    // login_1_ua.json
    return name + "_$schemeVersion" + "_$language.json"
}