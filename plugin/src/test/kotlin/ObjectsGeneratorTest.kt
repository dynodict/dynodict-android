import org.dynodict.plugin.generation.ObjectsGenerator
import org.dynodict.plugin.generation.StringModel
import org.dynodict.plugin.remote.DString
import org.junit.Test

class ObjectsGeneratorTest {
    @Test
    fun `one tree without parameters`() {
        val leaf = DString(key = "Name", value = "Login")
        val input = mapOf("LoginScreen" to StringModel(mutableMapOf("Login" to StringModel(value = leaf))))
        with(ObjectsGenerator("appendLine")) {
            val result = generate(input)
            println(result)
        }
    }
}