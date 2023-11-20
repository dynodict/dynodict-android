package org.dynodict.android

import android.content.Context
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import org.dynodict.DynoDict
import org.dynodict.DynodictCallback
import org.dynodict.EmptyDynodictCallback
import org.dynodict.android.provider.asset.AssetsDefaultDataProvider
import org.dynodict.model.settings.Settings

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