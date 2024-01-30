plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    `maven-publish`
}

//publishing {
//    publications {
//        create<MavenPublication>("maven") {
//            group = "org.dynodict"
//            artifactId = "library-core"
//            version = Versions.dynodictLibrary
//            from(components["java"])
//        }
//    }
//}

publishing {
    repositories {
        maven {
            name = "dynodict-android"
            url = uri("https://maven.pkg.github.com/OWNER/REPOSITORY")
            credentials {
                username = "mkovalyk"
                password = "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"
//                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
//                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
            group = "org.dynodict"
            version = Versions.dynodictLibrary
        }
    }
    publications {
        register<MavenPublication>("org.dynodict") {
            from(components["java"])
        }
    }
}

java {
    sourceCompatibility = Config.sourceCompat
    targetCompatibility = Config.targetCompat
}

dependencies {

    implementation(Deps.okhttp)
    implementation(Deps.serializationCore)
    implementation(Deps.coroutines)


    testImplementation(TestDeps.junit)
    testImplementation(TestDeps.mockitoKotlin)
    testImplementation(Deps.serializationJson)
}
