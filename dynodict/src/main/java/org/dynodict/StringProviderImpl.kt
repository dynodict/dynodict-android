package org.dynodict

//class StringProviderImpl(
//    private val storage: ObservableStorage,
//    private val defaultStorage: Storage,
//    private val settings: Settings
//) : StringProvider {
//    override fun setLocale(locale: DLocale) {
//    }
//
//    override fun get(string: DString): String {
//        val value = storage.value[string]
//
//        if (value != null) return value
//
//        return when (settings.fallbackStrategy) {
//            FallbackStrategy.ThrowException -> {
//                throw StringNotFoundException("Translation not found for path: ${string.key}")
//            }
//            FallbackStrategy.EmptyString -> {
//                ""
//            }
//            FallbackStrategy.ReturnDefault -> {
//                // It should never be null for default storage
//                defaultStorage.value[string]!!
//            }
//        }
//    }
//}