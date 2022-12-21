package org.dynodict.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class DownloadStringsPlugin : Plugin<Project> {
    override fun apply(target: Project) {

        target.logger.error("Applying plugin..")

        target.tasks.register<DownloadStringsTask>("downloadStrings") {
            val projectDirectory = target.layout.projectDirectory
            sourcesDirectory.set(projectDirectory.dir("src/main/kotlin"))
            assetsDirectory.set(projectDirectory.dir("src/main/assets"))
            println("Group: ${target.group}")
        }
    }
}