package org.dynodict.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.TaskAction

open class DownloadStringsTask @javax.inject.Inject constructor(objects: ObjectFactory)  : DefaultTask() {

    @TaskAction
    fun download() {
        println("DownloadStrings")
    }
}