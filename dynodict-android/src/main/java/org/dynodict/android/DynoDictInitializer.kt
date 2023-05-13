package org.dynodict.android

import android.content.Context
import androidx.startup.Initializer
import kotlinx.serialization.json.Json
import org.dynodict.DynoDict

class DynoDictInitializer : Initializer<DynoDict> {
    override fun create(context: Context): DynoDict {
        return DynoDict.initWith(context, endpoint = null, converter = Json)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }

}