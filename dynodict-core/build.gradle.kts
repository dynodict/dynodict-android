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
            version = "0.3"
            from(components["java"])
        }
    }
}
dependencies {

    // okhttp
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    // serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.4.1")

    // coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.8.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

}