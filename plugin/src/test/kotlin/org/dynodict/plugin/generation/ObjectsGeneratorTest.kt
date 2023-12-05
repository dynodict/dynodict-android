package org.dynodict.plugin.generation

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.dynodict.plugin.exception.IllegalTypeException
import org.dynodict.remote.model.bucket.RemoteBucket
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

    @Test
    fun `multiple similar items`() {
        val json = Json {
            prettyPrint = true
        }
        val items = json.decodeFromString<List<RemoteDString>>(INPUT)
        val treeInflater = TreeInflater()
        val bucket = RemoteBucket(
            schemeVersion = 1,
            editionVersion = 2,
            name = "login",
            translations = items
        )
        val tree = treeInflater.generateTree(bucket)

        val objectGenerator = ObjectsGenerator("org.dynodict.generated")

        println(objectGenerator.generate(tree, mutableSetOf()))
    }

    companion object {

        const val INPUT = """
                    [
        {
            "key": "App.Name",
            "value": "MoWid"
        },
        {
            "key": "Title.Home",
            "value": "MoWid"
        },
        {
            "key": "Label.Add",
            "value": "Add"
        },
        {
            "key": "Label.Edit",
            "value": "Edit"
        },
        {
            "key": "Label.Cancel",
            "value": "Cancel"
        },
        {
            "key": "Title.Add.Group",
            "value": "Add group"
        },
        {
            "key": "Title.Edit.Group",
            "value": "Edit group"
        },
        {
            "key": "Label.Group",
            "value": "Group"
        },
        {
            "key": "Label.Description",
            "value": "Description"
        },
        {
            "key": "Label.Delete.Group",
            "value": "Delete group?"
        },
        {
            "key": "Label.Delete.Group.Message",
            "value": "Are you sure you want to delete this group?"
        },
        {
            "key": "Title.Add.Quote",
            "value": "Add quote"
        },
        {
            "key": "Title.Edit.Quote",
            "value": "Edit quote"
        },
        {
            "key": "Label.Quote",
            "value": "Quote"
        },
        {
            "key": "Label.Author",
            "value": "Author (optional)"
        },
        {
            "key": "Label.Empty.State",
            "value": "There are no quotes yet.. Click this button below to add the first one"
        },
        {
            "key": "Label.Delete.Quote",
            "value": "Delete quote?"
        },
        {
            "key": "Label.Delete.Quote.Message",
            "value": "Are you sure you want to delete this quote?"
        },
        {
            "key": "Label.Delete",
            "value": "Delete"
        },
        {
            "key": "Label.Sign.In.Success",
            "value": "Signing in complete"
        },
        {
            "key": "Label.Sign.In.Error",
            "value": "Signing in error"
        },
        {
            "key": "Label.Sign.Out.Success",
            "value": "Signing out complete"
        },
        {
            "key": "Label.User.Signed.In.As",
            "value": "\"You are signed in as \""
        },
        {
            "key": "Label.User.Not.Registered",
            "value": "You are not registered, please "
        },
        {
            "key": "Label.Sign.In",
            "value": "SignIn"
        },
        {
            "key": "Label.Sign.Out",
            "value": "SignOut"
        },
        {
            "key": "Sign.In.Alert.Dialog.Text",
            "value": "To continue you should SignIn"
        },
        {
            "key": "Title.Settings",
            "value": "Settings"
        },
        {
            "key": "Label.Apply",
            "value": "Apply"
        },
        {
            "key": "Label.Frequency",
            "value": "Frequency"
        },
        {
            "key": "Label.Applied",
            "value": "Applied"
        },
        {
            "key": "Once.A.Week",
            "value": "1 time a week"
        },
        {
            "key": "Once.In.A.Five.Days",
            "value": "1 time in 5 days"
        },
        {
            "key": "Once.In.Two.Days",
            "value": "1 time in 2 days"
        },
        {
            "key": "Once.A.Day",
            "value": "1 time a day"
        },
        {
            "key": "Twice.A.Day",
            "value": "2 times a day"
        },
        {
            "key": "Fours.A.Day",
            "value": "4 times a day"
        }
    ]
        """

        const val HEADER = """package org.dynodict.generated

import org.dynodict.DynoDict
import org.dynodict.model.StringKey
import org.dynodict.model.Parameter

"""
        const val NO_PARAMS = """
object LoginScreen : StringKey("LoginScreen") {
    object Login : StringKey("Login", LoginScreen) {
        val value: String
            get() = DynoDict.instance.get(this)
    }
}
"""
        const val ONE_PARAM = """
object LoginScreen : StringKey("LoginScreen") {
    object Login : StringKey("Login", LoginScreen) {
        fun value(param1: String): String {
            return DynoDict.instance.get(this, Parameter.StringParameter(param1, key = "param1"))
        }
    }
}
"""

        const val ONE_PARAM_AND_CUSTOM_FORMAT = """
object LoginScreen : StringKey("LoginScreen") {
    object Login : StringKey("Login", LoginScreen) {
        fun value(param1: String): String {
            return DynoDict.instance.get(this, Parameter.StringParameter(param1, key = "param1", format = "loggedAt"))
        }
    }
}
"""
    }
}
