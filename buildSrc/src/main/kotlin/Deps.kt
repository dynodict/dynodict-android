import Versions.dynodictLibrary
import Versions.serialization
import org.gradle.api.JavaVersion

object Versions {
    // dynodict
    const val dynodictPlugin = "0.5.5"
    const val dynodictLibrary = "0.5.3"

    // network
    const val okhttp = "4.10.0"
    const val coroutines = "1.6.4"

    // Compose
    const val composeBom = "2023.01.00"
    const val material = "1.7.0"
    const val coreKtx = "1.9.0"
    const val constraint = "2.1.4"

    // test deps
    const val junit = "4.13.2"
    const val mockitoCore = "4.8.0"
    const val mockitoKotlin = "4.1.0"

    // initializer
    const val initializer: String = "1.1.1"

    // Serialization
    const val serialization = "1.4.1"

    // Yaml
    const val yaml = "0.13.0"
}

object Config {
    const val kotlin = "1.8.0"

    // depends on compose version
    const val kotlinCompiler = "1.4.0"
    const val gradlePlugin = "7.4.0"

    const val compileSdk = 33
    const val targetSdk = 33
    const val minSdk = 24

    const val versionCode = 1
    const val appVersion = "1.0"

    val sourceCompat = JavaVersion.VERSION_11
    val targetCompat = JavaVersion.VERSION_11
    val kotlinJvmTarget = "11"
}


object Deps {
    val dynodictCore by lazy { "org.dynodict:library-core:$dynodictLibrary" }
    val dynodictAndroid by lazy { "org.dynodict:library-android:$dynodictLibrary" }
    val okhttp by lazy { "com.squareup.okhttp3:okhttp:${Versions.okhttp}" }
    val serializationCore by lazy { "org.jetbrains.kotlinx:kotlinx-serialization-core:$serialization" }
    val serializationJson by lazy { "org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization" }
    val serializationYaml by lazy{"net.mamoe.yamlkt:yamlkt:${Versions.yaml}"}
    val initializer by lazy { "androidx.startup:startup-runtime:${Versions.initializer}" }
    val coroutines by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}" }
    val coroutinesAndroid by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}" }

    object Compose {
        val platform by lazy { "androidx.compose:compose-bom:${Versions.composeBom}" }
        val activity by lazy { "androidx.activity:activity-compose" }
        val uiTooling by lazy { "androidx.compose.ui:ui-tooling" }
        val uiPreview by lazy { "androidx.compose.ui:ui-tooling-preview" }
        val ui by lazy { "androidx.compose.ui:ui" }
        val material by lazy { "androidx.compose.material:material" }
        val foundation by lazy { "androidx.compose.foundation:foundation" }
    }

    object AndroidX {
        val coreKtx by lazy { "androidx.core:core-ktx:${Versions.coreKtx}" }
        val constraint by lazy { "androidx.constraintlayout:constraintlayout:${Versions.constraint}" }
    }

    object UI {
        val material by lazy { "com.google.android.material:material:${Versions.material}" }
    }
}

object TestDeps {
    val junit by lazy { "junit:junit:${Versions.junit}" }
    val mockitoCore by lazy { "org.mockito:mockito-core:${Versions.mockitoCore}" }
    val mockitoKotlin by lazy { "org.mockito.kotlin:mockito-kotlin:${Versions.mockitoKotlin}" }
}

