package org.dynodict.plugin.evaluator

import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File


class ParametersEvaluatorTest {

    private lateinit var evaluator: ParametersEvaluator
    private lateinit var projectDir: File

    @get:Rule
    var folder = TemporaryFolder()

    @Before
    fun before() {
        projectDir = folder.newFolder("project")
        evaluator = ParametersEvaluator(projectDir)
    }

    @Test
    fun `WHEN no assets folder is passed THEN default should be used`() {
        val result = evaluator.evaluateAssetsFolder("")
        assertTrue(result.endsWith("project/src/main/assets"))
    }

    @Test
    fun `WHEN assets folder is passed THEN it should be used as a reference`() {
        val result = evaluator.evaluateAssetsFolder("/src/main/assets/default")
        assertTrue(result.endsWith("project/src/main/assets/default"))
    }

    @Test
    fun `WHEN source folder is not passed THEN search in kotlin file`() {
        File(projectDir, "src/main/kotlin").mkdirs()

        val result = evaluator.evaluateSourcesAndPackage(sourcesFolder = "", packageName = "")
        assertTrue(result.first.endsWith("project/src/main/kotlin"))
    }

    @Test
    fun `WHEN source folder is not passed THEN search in java file`() {
        File(projectDir, "src/main/java").mkdirs()

        val result = evaluator.evaluateSourcesAndPackage(sourcesFolder = "", packageName = "")
        assertTrue(result.first.endsWith("project/src/main/java"))
    }

    @Test
    fun `WHEN source folder is not passed AND no default folder exists THEN exception should be thrown`() {
        try {
            evaluator.evaluateSourcesAndPackage(sourcesFolder = "", packageName = "")
            fail()
        } catch (ex: IllegalStateException) {
            // expected
        }
    }

    @Test
    fun `WNEN package and sources are passed THEN merged folder should be passed`() {
//        File(projectDir, "src/main/java/org/dynodict/strings").mkdirs()

        val result = evaluator.evaluateSourcesAndPackage(sourcesFolder = "src/main/java", packageName = "org.dynodict.strings")
        assertTrue(result.first.endsWith("src/main/java/org/dynodict/strings"))
        assertTrue(result.second.endsWith("org.dynodict.strings"))
    }

    @Test
    fun `WNEN package and sources not passed THEN auto evaluation should be used`(){
        File(projectDir, "src/main/java/org/dynodict").mkdirs()

        val result = evaluator.evaluateSourcesAndPackage("", "")
        assertTrue(result.first.endsWith("src/main/java/org/dynodict"))
        assertTrue(result.second.endsWith("org.dynodict"))
    }

    @Test
    fun `WNEN package and sources not passed AND folder with only 1 file THEN auto evaluation should be used`(){
        File(projectDir, "src/main/java/org/dynodict").mkdirs()
        File(projectDir, "src/main/java/org/dynodict/file.txt").createNewFile()

        val result = evaluator.evaluateSourcesAndPackage("", "")
        assertTrue(result.first.endsWith("src/main/java/org/dynodict"))
        assertTrue(result.second.endsWith("org.dynodict"))
    }

}