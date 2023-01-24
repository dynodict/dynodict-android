package org.dynodict.android

import android.content.Context
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import org.dynodict.DynoDict
import org.dynodict.android.provider.asset.AssetsDefaultDataProvider
import org.dynodict.model.settings.Settings

fun DynoDict.Companion.initWith(
    context: Context,
    endpoint: String?,
    settings: Settings = Settings.Default
): DynoDict {

    val converter = Json {
        // Ignore unknown keys
    }
    val defaultDataProvider = AssetsDefaultDataProvider(context.assets)
    return initWith(context, endpoint, settings, converter)
}

fun DynoDict.Companion.initWith(
    context: Context,
    endpoint: String?,
    settings: Settings = Settings.Default,
    converter: StringFormat
): DynoDict {

    val defaultDataProvider = AssetsDefaultDataProvider(context.assets)
    return initWith(endpoint, converter, context.filesDir, defaultDataProvider, settings)
}