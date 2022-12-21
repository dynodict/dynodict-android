package org.dynodict.plugin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.dynodict.plugin.generation.ObjectsGenerator
import org.dynodict.plugin.generation.StringModel
import org.dynodict.plugin.generation.TreeInflater
import org.dynodict.plugin.remote.Bucket
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
        }
        val remoteManager =
            RemoteManagerImpl(
                RemoteSettings("https://raw.githubusercontent.com/mkovalyk/GraphicEditor/master/"),
                json
            )
        runBlocking {

            coroutineScope.launch {
                // 1. Download metadata
                val metadata = remoteManager.getMetadata()
                    ?: throw IllegalStateException("There is an error during retrieving of the metadata")

                // copy metadata and set only default language
                val metadataWithDefault =
                    metadata.copy(languages = listOf(metadata.defaultLanguage))

                // 2. Download buckets
                val buckets = remoteManager.getStrings(metadataWithDefault)
                // 3. Generate sources based on the default metadata
                buckets.forEach {
                    mapBucket(it, json)
                }
                println("Got ${buckets.size} buckets")
            }
        }

        // 4. Copy default buckets to /assets folder
        println("Downloading finished")
    }

    private fun mapBucket(bucket: Bucket, json: Json) {

        val roots = treeInflater.generateTree(bucket)

        generateAndWrite(roots, sourcesDirectory)

        // Generate objects of Values
        val processed = bucket.translations.map {
            // clear params since it should not be used in resulting JSON
            it.copy(params = listOf())
        }

    }

    private fun generateAndWrite(
        roots: MutableMap<String, StringModel>,
        sourcesDirectory: DirectoryProperty
    ) {
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


    // LoginScreen.LoginButton
    // StringContainer("LoginScreen", Leaf("LoginButton", DString)
    //
    // Split by . to get a list of
    // Generate Strings.kt file based on the bucket info
    // Serialize to json file and copy to /assets folder
}