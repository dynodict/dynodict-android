tasks.create("deployCore") {
    dependsOn(":dynodict-core:assemble", ":dynodict-core:publishToMavenLocal")
    doLast {
        println("dynodict-core -> Deployed")
    }
}
tasks.create("deployAndroid") {
    dependsOn(":dynodict-android:assembleMaven", ":dynodict-android:publishToMavenLocal")
    doLast {
        println("dynodict-android -> Deployed")
    }
}
tasks.create("deployPlugin") {
    dependsOn(":plugin:assemble", ":plugin:publishToMavenLocal")
    doLast {
        println("plugin -> Deployed")
    }
}

// Task to deploy all libraries
tasks.create("deployAllLibraries") {
    dependsOn("clean", "deployPlugin", "deployAndroid")
}
