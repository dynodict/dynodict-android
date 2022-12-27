package org.dynodict.plugin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import org.dynodict.plugin.generation.ObjectsGenerator
import org.dynodict.plugin.generation.StringModel
import org.dynodict.plugin.generation.TreeInflater
import org.dynodict.plugin.remote.Bucket
import org.dynodict.plugin.remote.BucketsMetadata
import org.dynodict.plugin.remote.RemoteManagerImpl
import org.dynodict.plugin.remote.RemoteSettings
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

open class DownloadStringsTask @javax.inject.Inject constructor(objects: ObjectFactory) :
    DefaultTask() {

    private val coroutineScope = CoroutineScope(Job())

    @OutputDirectory
    val sourcesDirectory: DirectoryProperty = objects.directoryProperty()

    @OutputDirectory
    val assetsDirectory: DirectoryProperty = objects.directoryProperty()

    private val treeInflater = TreeInflater()

    @TaskAction
    fun download() {
        val json = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }

        println("Download started")
        val remoteManager =
            RemoteManagerImpl(
                RemoteSettings("https://raw.githubusercontent.com/mkovalyk/GraphicEditor/master/"),
                json
            )
        runBlocking {
            // 1. Download metadata
            val metadata = remoteManager.getMetadata()
                ?: throw IllegalStateException("There is an error during retrieving of the metadata")

            println("metadata: $metadata")
            // copy metadata and set only default language
            val metadataWithDefault =
                metadata.copy(languages = listOf(metadata.defaultLanguage))

            // 2. Download buckets
            val buckets = remoteManager.getStrings(metadataWithDefault)
            // 3. Generate sources based on the default metadata

            val assets = assetsDirectory.get().asFile

            buckets.forEach {
                mapBucket(it)
                // Generate objects of Values
                generateAssetsJson(it, json, assets)
            }

            println("Got ${buckets.size} buckets")

            writeMetadataToAssets(metadata, json, assets)
        }

        // 4. Copy default buckets to /assets folder
        println("Downloading finished")
    }

    private fun writeMetadataToAssets(metadata: BucketsMetadata, json: Json, assetsDirectory: File) {
        assetsDirectory.mkdirs()
        val file = File(assetsDirectory, "$PREFIX_DEFAULT_FILE$METADATA_NAME")
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()

        json.encodeToStream(metadata, file.outputStream())
        println("Metadata is copied")
    }

    private fun mapBucket(bucket: Bucket) {
        val roots = treeInflater.generateTree(bucket)
        generateSources(roots, sourcesDirectory)
    }

    private fun generateAssetsJson(bucket: Bucket, json: Json, assetsDirectory: File) {
        assetsDirectory.mkdirs()
        val processed = bucket.translations.map {
            // clear params since it should not be used in resulting JSON
            it.copy(params = listOf())
        }
        val clearedBucket = bucket.copy(translations = processed)

        val filename = bucket.generateFilename()
        val assetsFile = File(assetsDirectory, "$PREFIX_DEFAULT_FILE$filename")
        if (assetsFile.exists()) {
            assetsFile.delete()
        }
        assetsFile.createNewFile()

        json.encodeToStream(clearedBucket, assetsFile.outputStream())
        println("Assets file was copied")
    }

    private fun generateBucketName(name: String, language: String, schemeVersion: Int): String {
        // login_1_ua.json
        return name + "_$schemeVersion" + "_$language.json"
    }

    private fun Bucket.generateFilename(): String {
        // TODO handle name + language as nullable object
        return generateBucketName(name!!, language!!, schemeVersion)
    }

    private fun generateSources(roots: MutableMap<String, StringModel>, sourcesDirectory: DirectoryProperty) {
        val packageName = "org.dynodict"
        val result = ObjectsGenerator(packageName).generate(roots)
        val folder = File(sourcesDirectory.get().asFile, packageName.replace(".", "/"))
        folder.mkdirs()

        val file = File(folder, "Strings.kt")
        if (!file.exists()) {
            file.createNewFile()
        }
        file.writeText(result)
        println("Job done")
    }

    companion object {
        const val PREFIX_DEFAULT_FILE = "default_"
        const val METADATA_NAME = "metadata.json"
    }
}