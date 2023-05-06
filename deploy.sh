cd dynodict-core || exit
.././gradlew clean assemble publishToMavenLocal

# Sleep for some time to make sure core library is deployed properly
echo "Not finished yet.. Only first part is deployed. Please wait a bit more :)"
sleep 3
cd ../dynodict-android || exit

.././gradlew assembleMaven publishToMavenLocal

cd ../plugin || exit
.././gradlew assemble publishToMavenLocal

echo "Finished"