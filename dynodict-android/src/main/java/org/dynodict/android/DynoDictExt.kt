package org.dynodict.android

import android.content.Context
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import org.dynodict.DynoDict
import org.dynodict.DynodictCallback
import org.dynodict.EmptyDynodictCallback
import org.dynodict.android.provider.asset.AssetsDefaultDataProvider
import org.dynodict.formatter.DynoDictFormatter
import org.dynodict.manager.EmptyDynoDictManager
import org.dynodict.model.DLocale
import org.dynodict.model.Parameter
import org.dynodict.model.StringKey
import org.dynodict.model.settings.Settings
import org.dynodict.provider.StringProvider
import org.dynodict.storage.defaults.DefaultDataProvider
import java.io.File
import java.io.InputStream

fun DynoDict.Companion.initWith(
    context: Context,
    endpoint: String?,
    settings: Settings = Settings.Default
): DynoDict {

    val converter = Json {
        // Ignore unknown keys
        ignoreUnknownKeys = settings.ignoreUnknownKeys
    }
    return initWith(context, endpoint, converter, settings)
}

fun DynoDict.Companion.initWith(
    context: Context,
    endpoint: String?,
    converter: StringFormat,
    settings: Settings = Settings.Default,
    errorCallback: DynodictCallback = EmptyDynodictCallback
): DynoDict {

    val defaultDataProvider = AssetsDefaultDataProvider(context.assets)
    return initWith(endpoint, converter, context.filesDir, defaultDataProvider, errorCallback, settings)
}

fun DynoDict.Companion.initWithEmpty(): DynoDict {
    return initWith(EmptyStringProvider(), EmptyDynoDictManager())
}

class EmptyStringProvider: StringProvider {

    override fun get(key: StringKey, vararg parameters: Parameter): String {
        return key.toString()
    }

    override fun registerFormatter(key: String, value: DynoDictFormatter<*>?) {
        // Do nothing
    }

    override suspend fun setLocale(locale: DLocale) {
        // Do nothing
    }

}