package org.dynodict.plugin.evaluator

import java.io.File

/**
 * This class evaluates default folders to place generated data if no input parameters are passed
 *
 */
class ParametersEvaluator(private val projectDirectory: File) {

    fun evaluateSourcesAndPackage(sourcesFolder: String, packageName: String): Pair<File, String> {
        val sourcesFolderFile = evaluateSourceFolder(sourcesFolder)

        // identify package to place the files
        return if (packageName.isNotEmpty()) {
            val folder = packageName.replace(".", "/")
            Pair(File(sourcesFolderFile, folder), packageName)
        } else {
            var folder = sourcesFolderFile
            // So the logic is following:
            // If there is only one folder - go inside recursively until there is EITHER no folders OR more than one file.
            // It is needed to automatically identify the place to create files.
            // Usually, packages are hierarchically placed folders so it is safe to rely on this mechanism.
            // But is is still better to explicitly set sources and package names
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
            val evaluatedPackageName =
                folder.absolutePath.removePrefix(sourcesFolderFile.absolutePath)
                    .removePrefix("/")
                    .replace("/", ".")
            Pair(folder, evaluatedPackageName)
        }
    }

    private fun evaluateSourceFolder(sourcesFolder: String): File {
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
        return sourcesFolderFile
    }

    fun evaluateAssetsFolder(assetsFolder: String): File {
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