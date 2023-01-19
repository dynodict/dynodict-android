package org.dynodict.plugin

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import org.dynodict.plugin.evaluator.ParametersEvaluator
import org.dynodict.plugin.generation.ExtensionFunctionGenerator
import org.dynodict.plugin.generation.ObjectsGenerator
import org.dynodict.plugin.generation.StringModel
import org.dynodict.plugin.generation.TreeInflater
import org.dynodict.plugin.remote.Bucket
import org.dynodict.plugin.remote.BucketsMetadata
import org.dynodict.plugin.remote.RemoteManagerImpl
import org.dynodict.plugin.remote.RemoteSettings
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

open class DownloadStringsTask : DefaultTask() {

    @set:Option(
        option = "sources",
        description = "Directory to generate source files",
    )
    @get:Input
    var sourcesDir = ""

    @set:Option(
        option = "assets", description = "Directory to copy default assets files"
    )
    @get:Input
    var assetsDir = ""

    @set:Option(
        option = "url", description = "Url of the repository to download metadata and buckets"
    )
    @get:Input
    var url = "https://raw.githubusercontent.com/mkovalyk/GraphicEditor/master/"

    @set:Option(
        option = "package", description = "Package name for the app"
    )
    @get:Input
    var packageName: String = ""

    @OutputDirectory
    var projectDirectory: File = File("")

    private val treeInflater = TreeInflater()
    private val paramEvaluator = ParametersEvaluator()

    @TaskAction
    fun download() {
        val json = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }
        val outputDirectories = paramEvaluator.evaluateAndCreateIfNeeded(
            projectDirectory,
            assetsDir,
            sourcesDir,
            packageName)

        println("Download task. Folders - $outputDirectories")
        val remoteManager = RemoteManagerImpl(RemoteSettings(url), json)

        runBlocking {
            // 1. Download metadata
            val metadata = remoteManager.getMetadata()
                ?: throw IllegalStateException("There is an error during retrieving of the metadata")

            // copy metadata and set only default language
            val metadataWithDefault = metadata.copy(languages = listOf(metadata.defaultLanguage))

            // 2. Download buckets
            val buckets = remoteManager.getStrings(metadataWithDefault)

            val customFormats = mutableSetOf<String>()
            buckets.forEach {
                mapBucket(it, customFormats, outputDirectories.second, outputDirectories.third)
                generateAssetsJson(it, json, outputDirectories.first)
            }

            writeMetadataToAssets(metadata, json, outputDirectories.first)
        }
    }

    private fun writeMetadataToAssets(
        metadata: BucketsMetadata, json: Json, assetsDirectory: File
    ) {
        assetsDirectory.mkdirs()
        val file = File(assetsDirectory, "$PREFIX_DEFAULT_FILE$METADATA_NAME")
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()

        json.encodeToStream(metadata, file.outputStream())
    }

    private fun mapBucket(
        bucket: Bucket,
        customFormats: MutableSet<String>,
        sourceDir: File,
        packageName: String
    ) {
        val roots = treeInflater.generateTree(bucket)
        generateSources(roots, sourceDir, customFormats, packageName)
    }

    private fun generateAssetsJson(bucket: Bucket, json: Json, assetsDirectory: File) {
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
    }

    private fun generateBucketName(name: String, language: String, schemeVersion: Int): String {
        // login_1_ua.json
        return name + "_$schemeVersion" + "_$language.json"
    }

    private fun Bucket.generateFilename(): String {
        return generateBucketName(name!!, language!!, schemeVersion)
    }

    private fun generateSources(
        roots: MutableMap<String, StringModel>,
        folder: File,
        customFormats: MutableSet<String>,
        packageName: String,
    ) {
        val result = ObjectsGenerator(packageName).generate(roots, customFormats)

        val file = File(folder, STRINGS_NAME)
        if (!file.exists()) {
            file.createNewFile()
        }
        file.writeText(result)

        if (customFormats.isNotEmpty()) {
            val extensionFile = File(folder, DYNODICT_EXT_NAME)

            if (!extensionFile.exists()) {
                extensionFile.createNewFile()
            }
            val extensionText =
                ExtensionFunctionGenerator(packageName).generate(customFormats.toList())
            extensionFile.writeText(extensionText)
        }
    }

    companion object {
        const val PREFIX_DEFAULT_FILE = "default_"
        const val METADATA_NAME = "metadata.json"
        const val DYNODICT_EXT_NAME = "DynoDictExt.kt"
        const val STRINGS_NAME = "Strings.kt"
    }
}