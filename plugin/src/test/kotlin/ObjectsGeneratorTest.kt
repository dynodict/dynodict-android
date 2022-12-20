import org.dynodict.plugin.generation.ObjectsGenerator
import org.dynodict.plugin.generation.StringModel
import org.dynodict.plugin.remote.DString
import org.junit.Assert.assertEquals
import org.junit.Test

class ObjectsGeneratorTest {

    @Test
    fun `one tree without parameters`() {
        val leaf = DString(key = "Name", value = "Login")
        val input =
            mapOf("LoginScreen" to StringModel(mutableMapOf("Login" to StringModel(value = leaf))))
        with(ObjectsGenerator("org.dynodict.generated")) {
            val result = generate(input)
            println(result)
            assertEquals(NO_PARAMS, result)
        }
    }

    companion object {
        const val NO_PARAMS = """package org.dynodict.generated

import org.dynodict.DynoDict
import org.dynodict.model.StringKey
import org.dynodict.model.Key

object LoginScreen : StringKey("LoginScreen" ) {
    object Login : StringKey("Login", LoginScreen ) {
        fun get(): String {
            return DynoDict.instance.get(Key(absolutePath))
        }
    }
}
"""
    }
}
