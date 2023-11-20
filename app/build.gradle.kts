plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // If this plugin can not be found i.e. this error happens:
    // Plugin [id: 'org.dynodict.plugin', version: '0.5.2', apply: false] was not found in any of the following sources:
    // Do the following:
    // 1. Comment out this line
    // 3. Run ./deploy.sh
    // 4. Uncomment this line and perform sync once again
    id("org.dynodict.plugin") version Versions.dynodictPlugin
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
                "proguard-rules.pro"
            )
        }
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

    "mavenImplementation"(Deps.dynodictAndroid)
    "mavenImplementation"(Deps.dynodictCore)
    "projectImplementation"(project(":dynodict-android"))
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

apply {
    from("../deploy.gradle.kts")
}
