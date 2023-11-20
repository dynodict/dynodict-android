package org.dynodict.plugin

import org.dynodict.plugin.download.DownloadStringsTask
import org.dynodict.plugin.migration.MigrateStringsTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

@Suppress("unused")
class DynodictPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.register<DownloadStringsTask>("downloadStrings") {
            val directory = target.layout.projectDirectory.asFile
            projectDirectoryParam = directory
        }
        target.tasks.register<MigrateStringsTask>("migrateStrings") {
            val directory = target.layout.projectDirectory.asFile
            projectDirectoryParam = directory
        }
    }
}