package org.dynodict.plugin.migration

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.dynodict.model.metadata.BucketMetadata
import org.dynodict.model.metadata.BucketsMetadata
import org.dynodict.plugin.ext.generateBucketName
import org.dynodict.plugin.xml.entity.ResourcesParser
import org.dynodict.remote.model.bucket.RemoteBucket
import org.dynodict.remote.model.bucket.RemoteParameter
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

open class MigrateStringsTask : DefaultTask() {

    // choose file to parse from
    // read the option which indicates how the file should be parsed "domain_subdomain_detail"
    // read the option which indicates how the name should be stored. Hierarchy: "domain", "subdomain", "detail"
    // parse xml file +
    // convert xml resources to string model +
    // store result in json/yaml file? +
    @set:Option(
        option = "input",
        description = "Default is strings.xml",
    )
    @get:Input
    var inputDirParam = ""

    @set:Option(
        option = "output",
        description = "It will place output in this folder. Default is ''",
    )
    @get:Input
    var outputDirParam = ""


    @set:Option(
        option = "edition",
        description = "Edition number. Resulting file will be named as 'bucketName-locale-schemeVersion.json'. Default is 1",
    )
    @get:Input
    var schemeVersion = "1"

    private val schemeVersionInt
        get() = schemeVersion.toInt()

    @set:Option(
        option = "bucketName",
        description = "Name of the bucket. Resulting file will be named as " +
            "'bucketName-locale-schemeVersion.json'. Default is default",
    )
    @get:Input
    var bucketName = "strings"

    @OutputDirectory
    var projectDirectoryParam: File = File("")

    @TaskAction
    fun convert() {

        try {
            val file = File(inputDirParam)
            val strings = ResourcesParser().parse(file.readText())
            // serialize
            val json = Json { prettyPrint = true }

            val locale = inputDirParam.split("values-").getOrNull(1) ?: "en"

            if (validateOutputParameter(outputDirParam)) {
                val outputFolder = File(projectDirectoryParam, outputDirParam)
                val jsonFile = File(outputFolder, generateBucketName(bucketName, locale, schemeVersionInt))

                val bucket = RemoteBucket(
                    schemeVersion = schemeVersionInt,
                    editionVersion = 1,
                    name = bucketName,
                    language = locale,
                    translations = strings
                )
                val result = json.encodeToString(bucket)
                jsonFile.writeText(result)

                putMetadata(outputFolder, locale, json, schemeVersionInt, bucketName)
            }
        } catch (e: Exception) {
            println("Exception: $e")
        }
    }

    private fun putMetadata(
        outputFolder: File,
        locale: String,
        yaml: Json,
        schemeVersion: Int,
        bucketName: String,
    ) {
        val metadataFile = File(outputFolder, METADATA_NAME)
        val metadata = BucketsMetadata(
            defaultLanguage = locale,
            languages = listOf(locale),
            buckets = listOf(
                BucketMetadata(
                    name = bucketName,
                    language = locale,
                    schemeVersion = schemeVersion,
                    editionVersion = 1
                )
            )
        )
        println("Output File: ${metadataFile.absolutePath}")
        val metadataResult = yaml.encodeToString(metadata)
        metadataFile.writeText(metadataResult)
    }

    private fun validateOutputParameter(param: String): Boolean {
        if (param.contains(".json")) {
            println("Output parameter is not valid. It should be a folder")
            return false
        } else {
            if (param.isNotEmpty()) {
                val file = File(projectDirectoryParam, param)
                if (!file.exists()) {
                    file.mkdirs()
                }
            }
        }
        return true
    }

    companion object {

        const val PREFIX_DEFAULT_FILE = "default_"
        const val METADATA_NAME = "metadata.json"
        const val DYNODICT_EXT_NAME = "DynoDictExt.kt"
        const val STRINGS_NAME = "Strings.kt"
    }
}


sealed class Translation {
    data class Leaf(val key: String, val value: String, val params: List<RemoteParameter>) : Translation()
    data class Container(val key: String, val children: List<Translation>) : Translation()
}