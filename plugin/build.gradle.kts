plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    id("org.jetbrains.kotlin.plugin.serialization")
}

gradlePlugin {
    plugins {
        register("downloadStringsPlugin") {
            group = "org.dynodict"
            id = "org.dynodict.plugin"
            implementationClass = "org.dynodict.plugin.DownloadStringsPlugin"
        }
    }
}

version = Versions.dynodictPlugin

publishing {
    publications {
        create<MavenPublication>("maven") {
            group = "org.dynodict"
            artifactId = "plugin"
            version = Versions.dynodictPlugin
            from(components["kotlin"])
        }
    }
}

java {
    sourceCompatibility = Config.sourceCompat
    targetCompatibility = Config.targetCompat
}

dependencies {
    implementation(Deps.dynodictCore)
    implementation(Deps.okhttp)

    implementation(Deps.serializationJson)
    implementation(Deps.coroutines)

    testImplementation(TestDeps.junit)
    testImplementation(TestDeps.mockitoKotlin)
}

tasks.getByName("publishToMavenLocal").dependsOn("clean", "assemble")
