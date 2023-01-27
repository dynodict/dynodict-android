plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
//    id("plugin")
}

android {
    namespace = "org.dynodict"
    compileSdk = Config.compileSdk

    defaultConfig {
        applicationId = "org.dynodict"
        minSdk = Config.minSdk
        targetSdk = Config.targetSdk
        versionCode = Config.versionCode
        versionName = Config.appVersion
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = Config.sourceCompat
        targetCompatibility = Config.targetCompat
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Config.kotlinCompiler
    }
    kotlinOptions {
        jvmTarget = Config.kotlinJvmTarget
    }
}

dependencies {

    implementation(Deps.dynodictAndroid)
    implementation(Deps.serializationJson)

    implementation(Deps.UI.material)

    val composeBom = platform(Deps.Compose.platform)
    implementation(composeBom)
    implementation(Deps.Compose.activity)
    implementation(Deps.Compose.uiPreview)
    implementation(Deps.Compose.ui)
    implementation(Deps.Compose.material)
    implementation(Deps.Compose.foundation)

    implementation(Deps.AndroidX.coreKtx)
    implementation(Deps.AndroidX.constraint)

    debugImplementation(Deps.Compose.uiTooling)

    testImplementation(TestDeps.junit)
}

//apply {
//    from("../deploy.gradle.kts")
//}

tasks.create("deployCore") {
    dependsOn(":dynodict-core:assemble", ":dynodict-core:publishToMavenLocal")
    doLast {
        println("dynodict-core -> Deployed")
    }
}
tasks.create("deployAndroid") {
    dependsOn(":dynodict-android:assemble", ":dynodict-android:publishToMavenLocal")
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
    mustRunAfter("deployCore")
    dependsOn("clean", "deployPlugin", "deployAndroid")
}