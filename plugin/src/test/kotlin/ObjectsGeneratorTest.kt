import org.dynodict.plugin.generation.ObjectsGenerator
import org.dynodict.plugin.generation.StringModel
import org.dynodict.plugin.remote.DString
import org.dynodict.plugin.remote.Parameter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ObjectsGeneratorTest {

    @Test
    fun `imports and package should be properly inflated`() {
        val leaf = DString(key = "Name", value = "Login")
        val input =
            mapOf("LoginScreen" to StringModel(mutableMapOf("Login" to StringModel(value = leaf))))
        with(ObjectsGenerator("org.dynodict.generated")) {
            val result = generate(input)
            println(result)
            assertTrue(result.startsWith(HEADER))
        }
    }

    @Test
    fun `one tree without parameters`() {
        val leaf = DString(key = "Name", value = "Login")
        val input =
            mapOf("LoginScreen" to StringModel(mutableMapOf("Login" to StringModel(value = leaf))))
        with(ObjectsGenerator("org.dynodict.generated")) {
            val result = generate(input)
            println(result)
            assertTrue(result.contains(NO_PARAMS))
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
            assertTrue(result.contains(ONE_PARAM))
        }
    }

    @Test
    fun `one tree with one String parameter and custom format`() {
        val leaf = DString(
            key = "Name",
            value = "Login",
            params = listOf(Parameter(key = "param1", type = "String", format = "loggedAt")))
        val input =
            mapOf("LoginScreen" to StringModel(mutableMapOf("Login" to StringModel(value = leaf))))
        with(ObjectsGenerator("org.dynodict.generated")) {
            val result = generate(input)
            println(result)
            assertTrue(result.contains(ONE_PARAM_AND_CUSTOM_FORMAT))
        }
    }

    companion object {

        const val HEADER = """package org.dynodict.generated

import org.dynodict.DynoDict
import org.dynodict.model.StringKey
import org.dynodict.model.Key
import org.dynodict.model.Parameter

"""
        const val NO_PARAMS = """
object LoginScreen : StringKey("LoginScreen") {
    object Login : StringKey("Login", LoginScreen) {
        fun get(): String {
            return DynoDict.instance.get(Key(absolutePath, params = listOf()))
        }
    }
}
"""
        const val ONE_PARAM = """
object LoginScreen : StringKey("LoginScreen") {
    object Login : StringKey("Login", LoginScreen) {
        fun get(param1: String): String {
            return DynoDict.instance.get(Key(absolutePath, params = listOf(Parameter.StringParameter(param1))))
        }
    }
}
"""

        const val ONE_PARAM_AND_CUSTOM_FORMAT = """
object LoginScreen : StringKey("LoginScreen") {
    object Login : StringKey("Login", LoginScreen) {
        fun get(param1: String): String {
            return DynoDict.instance.get(Key(absolutePath, params = listOf(Parameter.StringParameter(param1, format = "loggedAt"))))
        }
    }
}
"""
    }
}
