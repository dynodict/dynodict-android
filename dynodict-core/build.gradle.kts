plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("maven-publish")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            group = "org.dynodict"
            artifactId = "library-core"
            version = Versions.dynodictLibrary
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
