package org.dynodict.plugin.evaluator

import java.io.File

/**
 * This class evaluates default folders to place generated data if no input parameters are passed
 *
 */
class ParametersEvaluator {

    /**
     * First - assets folder
     * Second - sources folder
     * Third - package name
     */
    fun evaluateAndCreateIfNeeded(
        projectDirectory: File,
        assetsFolder: String,
        sourcesFolder: String,
        packageName: String
    ): Triple<File, File, String> {
        val assetsFolderFile = evaluateAssetsFolder(projectDirectory, assetsFolder)
        val sourcesFolderFile = evaluateSourcesFolder(projectDirectory, sourcesFolder, packageName)

        return Triple(assetsFolderFile, sourcesFolderFile.first, sourcesFolderFile.second)
    }

    private fun evaluateSourcesFolder(
        projectDirectory: File,
        sourcesFolder: String,
        packageName: String
    ): Pair<File, String> {
        val sourcesFolderFile = if (sourcesFolder.isNotEmpty())
            File(projectDirectory, sourcesFolder)
        else {
            var temp = File(projectDirectory, DEFAULT_SOURCES_KOTLIN_FOLDER)
            if (!temp.exists()) {
                temp = File(projectDirectory, DEFAULT_SOURCES_JAVA_FOLDER)
                if (!temp.exists()) {
                    throw IllegalStateException("Can't find any source Folder")
                }
            }
            temp
        }

        // identify package to place the files
        return if (packageName.isNotEmpty()) {
            val folder = packageName.replace(".", "/")
            Pair(File(sourcesFolderFile, folder), packageName)
        } else {
            var folder = sourcesFolderFile
            while (folder.listFiles().orEmpty().size == 1) {
                val child = folder.listFiles()!![0]
                if (child.isDirectory) {
                    folder = child
                } else {
                    break
                }
            }
            // "/org/dynodict -> org.dynodict
            // It should be evaluated as a separate value because it might not always be passed
            // if the packageName is passed directly - Result will be p
            val evaluatedPackageName =
                folder.absolutePath.removePrefix(sourcesFolderFile.absolutePath)
                    .removePrefix("/")
                    .replace("/", ".")
            Pair(folder, evaluatedPackageName)
        }
    }

    private fun evaluateAssetsFolder(
        projectDirectory: File,
        assetsFolder: String
    ): File {
        val assetsFolderFile =
            File(projectDirectory, assetsFolder.ifEmpty { DEFAULT_ASSETS_FOLDER })
        if (!assetsFolderFile.exists()) {
            assetsFolderFile.mkdirs()
        }
        return assetsFolderFile
    }


    companion object {
        const val DEFAULT_ASSETS_FOLDER = "/src/main/assets"
        const val DEFAULT_SOURCES_JAVA_FOLDER = "/src/main/java"
        const val DEFAULT_SOURCES_KOTLIN_FOLDER = "/src/main/kotlin"
    }
}