plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
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
    repositories{
        mavenLocal()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

apply<org.dynodict.plugin.DownloadStringsPlugin>()