package org.dynodict.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

@Suppress("unused")
class DownloadStringsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.register<DownloadStringsTask>("downloadStrings") {
            val directory = target.layout.projectDirectory.asFile
            projectDirectoryParam = directory
        }
    }
}