plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    `maven-publish`
}

android {
    namespace = "org.dynodict.android"
    compileSdk = Config.compileSdk

    defaultConfig {
        minSdk = Config.minSdk
        targetSdk = Config.targetSdk
        version = Versions.dynodictLibrary
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = Config.sourceCompat
        targetCompatibility = Config.targetCompat
    }
    kotlinOptions {
        jvmTarget = Config.kotlinJvmTarget
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.dynodict"
            artifactId = "library-android"
            version = Versions.dynodictLibrary
            artifact("${project.buildDir}/outputs/aar/dynodict-android-release.aar")
            pom.withXml {
                val dependenciesNode = asNode().appendNode("dependencies")
                configurations.implementation.get().allDependencies.forEach { dependency ->
                    dependenciesNode.appendNode("dependency").apply {
                        appendNode("groupId", dependency.group)
                        appendNode("artifactId", dependency.name)
                        appendNode("version", dependency.version)
                    }
                }
            }
        }
    }
}
dependencies {
    implementation(Deps.dynodictCore)
    implementation(Deps.serializationJson)
}

tasks.create("publishToLocal") {
    dependsOn("clean", "assemble")
}
