package org.dynodict

/**
 * 1-st level:
 * Is used to provide simple API for a user to manage translations
 * Using it metadata will be first downloaded and then all the buckets downloaded as well
 */
interface SimpleManager : TranslationManager {
    fun setEndpoint(endpoint: String)
    fun updateTranslations()
}

/**
 * 2-nd level:
 * It contains more options to
 *  - set RemoteAgent which retrieves and parses the data
 *  - manually request buckets
 */
interface AdjustableManager : TranslationManager {
    val agent: RemoteAgent

    /**
     * Result will be flattened with absolutePath
     */
    suspend fun downloadTranslations(info: List<BucketInfo>): List<Translation>
}

/**
 * 3-rd level:
 * It doesn't know anything about retrieving of the Translations. Its responsibility is just to add/replace translation
 * in persistent storage
 */
interface AdvancedManager : TranslationManager {
    val locale: TranslationsLocale
    val storage: ObservableStorage

    fun addBucket(bucketInfo: BucketInfo, container: TranslationBucket)
    fun replaceAllTranslations(items: Map<BucketInfo, TranslationBucket>)
}

interface TranslationManager
