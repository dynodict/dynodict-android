package org.dynodict.manager

interface DynoDictManager {
    suspend fun updateTranslations()
//    suspend fun getMetadata(): BucketsMetadata?
//    suspend fun getAllForLanguage(language: String): List<DString>
}