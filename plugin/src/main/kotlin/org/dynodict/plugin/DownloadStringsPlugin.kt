package org.dynodict.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class DownloadStringsPlugin: Plugin<Project> {
    override fun apply(target: Project) {

        target.logger.error("Applying plugin..")

        target.tasks.register<DownloadStringsTask>("downloadStrings"){

        }
    }
}