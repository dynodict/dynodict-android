plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    `maven-publish`
    `signing`
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            pom {
                name.set("Dynodict Core")
                description.set("Core library for Dynodict")
                url.set("https://github.com/dynodict/dynodict-android/tree/main/dynodict-core")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("mkovalyk")
                        name.set("Mykhailo Kovalyk")
                        email.set("mishakovalyk@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/dynodict/dynodict-android.git")
                    developerConnection.set("scm:git:ssh://github.com/dynodict/dynodict-android.git")
                    url.set("https://github.com/dynodict/dynodict-android/tree/main/dynodict-core")
                }
            }
            group = "org.dynodict"
            artifactId = "library-core"
            version = Versions.dynodictLibrary
            from(components["java"])
        }
    }
}
signing{
    sign(publishing.publications)
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
