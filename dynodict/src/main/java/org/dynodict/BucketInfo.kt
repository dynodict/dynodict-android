package org.dynodict

data class BucketInfo(val version: Int, val bucketName: String, val localeList: List<TranslationsLocale>)
