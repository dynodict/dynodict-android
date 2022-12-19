plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
//    kotlin("jvm") version "1.6.21"
    id("org.jetbrains.kotlin.plugin.serialization").version("1.7.20")
}

group = "org.dynodict.plugin"
version = "0.4.3"

gradlePlugin {
    plugins {
        register("download-strings") {
            id = "org.dynodict.plugin"
            implementationClass = "org.dynodict.plugin.DownloadStringsPlugin"
        }
    }
}


publishing {
    repositories {
        mavenLocal()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    // define a BOM and its version
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.10.0"))

    // define any required OkHttp artifacts without version
    implementation("com.squareup.okhttp3:okhttp")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.8.0")
    testImplementation ("org.mockito.kotlin:mockito-kotlin:4.0.0")
}

//apply<org.dynodict.plugin.DownloadStringsPlugin>()