package org.dynodict

interface Dynodict {
    fun get(translation: Translation): String

    val processor: DynodictProcessor
}

interface Translation {
    val absolutePath: String

    val value: String

    companion object {
        const val DIVIDER = "/"
    }
}

interface TranslationContainer : Translation {
    val translations: List<Translation>
}

interface ParsingValue {
    val absolutePath: String
}

interface DynodictConverter {
    // "Any" because of it can be different converter
    fun convert(value: Any): List<Translation>
}

interface DynodictProcessor {
    fun addTranslationContainer(container: TranslationContainer)
    fun updateTranslations(containers: List<TranslationContainer>)
}

interface DynodictRetriever {
    suspend fun retrieveTranslations(): List<Translation>
}

interface ErrorHandler {
    /**
     * Is invoked when the error happens.
     * @return true exception is handled and should be propagated
     */
    fun onErrorOccurred(ex: Exception): ExceptionResolution
}

enum class ExceptionResolution {
    Handled, NotHandled
}

class DynoDictManager(
    var errorHandler: ErrorHandler? = null
) {
    fun updateTranslations()
}

enum class TranslationsLocale {
    EN, DA,
    // TODO add extension on Locale
}

Dynodict,
Builder
    Settings
Manager
    Converter/Parser
    Storage
    ZipInputStream
    ErrorHandler
    TranslationsRetriever

DynoStringProvider
    ObservableStorage


data class Settings(val fallbackStrategy: FallbackStrategy)

enum class FallbackStrategy {
    ThrowException, EmptyString, ReturnDefault
}

interface Storage {
    var value: List<Translation>
}

interface ObservableStorage : Storage {
    var listener: (() -> Unit)?
}