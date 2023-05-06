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

    flavorDimensionList += "type"

    productFlavors {
        create("project") {
            dimension = "type"
            matchingFallbacks += "project"
        }
        create("maven") {
            dimension = "type"
            matchingFallbacks += "maven"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.dynodict"
            artifactId = "library-android"
            version = Versions.dynodictLibrary
            artifact("${project.buildDir}/outputs/aar/dynodict-android-maven-release.aar")
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

    "mavenApi"(Deps.dynodictCore)
    "projectApi"(project(":dynodict-core"))
    implementation(Deps.serializationJson)
}

tasks.create("publishToLocal") {
    dependsOn("clean", "assemble")
}
