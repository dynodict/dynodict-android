package org.dynodict.android.provider.asset

import android.content.res.AssetManager
import org.dynodict.storage.defaults.DefaultDataProvider
import java.io.InputStream

class AssetsDefaultDataProvider(val assetManager: AssetManager) : DefaultDataProvider {
    override fun open(filename: String): InputStream {
        return assetManager.open(filename)
    }
}