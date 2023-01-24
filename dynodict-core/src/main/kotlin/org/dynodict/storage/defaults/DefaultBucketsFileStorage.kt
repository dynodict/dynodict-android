package org.dynodict.storage.defaults

import kotlinx.serialization.StringFormat
import org.dynodict.model.Bucket
import org.dynodict.storage.FileBucketsStorage
import org.dynodict.storage.copyAndClose
import java.io.File

class DefaultBucketsFileStorage(
    folder: File,
    json: StringFormat,
    private val defaultDataProvider: DefaultDataProvider,
) : FileBucketsStorage(folder, json) {
    override suspend fun get(filename: String): Bucket? {
        // default_login_1_ua.json
        val changedFilename = "$PREFIX_DEFAULT_FILE$filename"
        val defaultFile = File(folder, changedFilename)
        // if there is no file with such name - copy from assets
        if (!defaultFile.exists()) {
            val assetFile = defaultDataProvider.open(changedFilename)
            assetFile.copyAndClose(defaultFile)
        }

        return super.get(changedFilename)
    }

    override suspend fun save(bucket: Bucket) {
        throw IllegalStateException("Save operation can not be performed from this class")
    }


    companion object {
        const val PREFIX_DEFAULT_FILE = "default_"
    }
}