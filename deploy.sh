./gradlew :dynodict-core:clean :dynodict-core:assemble :dynodict-core:publishToMavenLocal
./gradlew --refresh-dependencies :dynodict-android:assemble :dynodict-android:publishToMavenLocal
./gradlew :plugin:assemble :plugin:publishToMavenLocal