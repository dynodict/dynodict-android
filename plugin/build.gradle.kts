plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    id("org.jetbrains.kotlin.plugin.serialization")
}

gradlePlugin {
    plugins {
        register("downloadStringsPlugin") {
            id = "org.dynodict.plugin"
            implementationClass = "org.dynodict.plugin.DownloadStringsPlugin"
        }
    }
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            group = "org.dynodict"
            artifactId = "plugin"
            version = "0.5.1"
            from(components["kotlin"])
        }
    }
    repositories {
        mavenLocal()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation("org.dynodict:library-core:0.3")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.8.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.8.0")
}