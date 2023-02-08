package org.dynodict.plugin.generation

import org.dynodict.plugin.exception.IllegalTypeException
import org.dynodict.remote.model.bucket.RemoteDString
import org.dynodict.remote.model.bucket.RemoteParameter
import org.junit.Assert.*
import org.junit.Test

class ObjectsGeneratorTest {

    @Test
    fun `imports and package should be properly inflated`() {
        val leaf = RemoteDString(key = "Name", value = "Login")
        val input =
            mapOf("LoginScreen" to StringModel(mutableMapOf("Login" to StringModel(value = leaf))))
        with(ObjectsGenerator("org.dynodict.generated")) {
            val customFormats = mutableSetOf<String>()
            val result = generate(input, customFormats)
            println(result)
            assertTrue(result.startsWith(HEADER))
        }
    }

    @Test
    fun `one tree without parameters`() {
        val leaf = RemoteDString(key = "Name", value = "Login")
        val input =
            mapOf("LoginScreen" to StringModel(mutableMapOf("Login" to StringModel(value = leaf))))
        with(ObjectsGenerator("org.dynodict.generated")) {
            val customFormats = mutableSetOf<String>()
            val result = generate(input, customFormats)
            println(result)
            assertTrue(result.contains(NO_PARAMS))
        }
    }


    @Test
    fun `one tree with one String parameter`() {
        val leaf = RemoteDString(
            key = "Name",
            value = "Login",
            params = listOf(RemoteParameter(key = "param1", type = "String"))
        )
        val input =
            mapOf("LoginScreen" to StringModel(mutableMapOf("Login" to StringModel(value = leaf))))
        with(ObjectsGenerator("org.dynodict.generated")) {
            val customFormats = mutableSetOf<String>()
            val result = generate(input, customFormats)
            println(result)
            assertTrue(result.contains(ONE_PARAM))
        }
    }

    @Test
    fun `one tree with one String parameter and custom format`() {
        val leaf = RemoteDString(
            key = "Name",
            value = "Login",
            params = listOf(RemoteParameter(key = "param1", type = "String", format = "loggedAt"))
        )
        val input =
            mapOf("LoginScreen" to StringModel(mutableMapOf("Login" to StringModel(value = leaf))))
        with(ObjectsGenerator("org.dynodict.generated")) {
            val customFormats = mutableSetOf<String>()
            val result = generate(input, customFormats)
            println(result)
            assertTrue(result.contains(ONE_PARAM_AND_CUSTOM_FORMAT))
            assertEquals(1, customFormats.size)
            assertEquals("loggedAt", customFormats.first())
        }
    }

    @Test
    fun `incorrect parameter type`() {
        val leaf = RemoteDString(
            key = "Name",
            value = "Login",
            params = listOf(RemoteParameter(key = "param1", type = "Integer"))
        )
        val input =
            mapOf("LoginScreen" to StringModel(mutableMapOf("Login" to StringModel(value = leaf))))
        with(ObjectsGenerator("org.dynodict.generated")) {
            try {
                val customFormats = mutableSetOf<String>()
                generate(input, customFormats)
                fail()
            } catch (ex: IllegalTypeException) {
                // expected
            }
        }
    }

    companion object {

        const val HEADER = """package org.dynodict.generated

import org.dynodict.DynoDict
import org.dynodict.model.StringKey
import org.dynodict.model.Parameter

"""
        const val NO_PARAMS = """
object LoginScreen : StringKey("LoginScreen") {
    object Login : StringKey("Login", LoginScreen) {
        fun get(): String {
            return DynoDict.instance.get(this)
        }
    }
}
"""
        const val ONE_PARAM = """
object LoginScreen : StringKey("LoginScreen") {
    object Login : StringKey("Login", LoginScreen) {
        fun get(param1: String): String {
            return DynoDict.instance.get(this, Parameter.StringParameter(param1, key = "param1"))
        }
    }
}
"""

        const val ONE_PARAM_AND_CUSTOM_FORMAT = """
object LoginScreen : StringKey("LoginScreen") {
    object Login : StringKey("Login", LoginScreen) {
        fun get(param1: String): String {
            return DynoDict.instance.get(this, Parameter.StringParameter(param1, key = "param1", format = "loggedAt"))
        }
    }
}
"""
    }
}
