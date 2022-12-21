import org.dynodict.plugin.generation.ObjectsGenerator
import org.dynodict.plugin.generation.StringModel
import org.dynodict.plugin.remote.DString
import org.dynodict.plugin.remote.Parameter
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

    @Test
    fun `one tree with one String parameter`() {
        val leaf = DString(
            key = "Name",
            value = "Login",
            params = listOf(Parameter(key = "param1", type = "String")))
        val input =
            mapOf("LoginScreen" to StringModel(mutableMapOf("Login" to StringModel(value = leaf))))
        with(ObjectsGenerator("org.dynodict.generated")) {
            val result = generate(input)
            println(result)
            assertEquals(ONE_PARAM, result)
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
        const val ONE_PARAM = """package org.dynodict.generated

import org.dynodict.DynoDict
import org.dynodict.model.StringKey
import org.dynodict.model.Key

object LoginScreen : StringKey("LoginScreen" ) {
    object Login : StringKey("Login", LoginScreen ) {
        fun get(param1: String): String {
            return DynoDict.instance.get(Key(absolutePath))
        }
    }
}
"""
    }
}
