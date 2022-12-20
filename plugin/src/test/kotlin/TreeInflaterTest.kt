import org.dynodict.plugin.generation.StringModel
import org.dynodict.plugin.generation.TreeInflater
import org.dynodict.plugin.remote.Bucket
import org.dynodict.plugin.remote.DString
import org.junit.Assert.assertEquals
import org.junit.Test

class TreeInflaterTest {

    @Test
    fun test() {
        val task = TreeInflater()
        val bucket = Bucket(
            schemeVersion = 1,
            editionVersion = 2,
            name = "login",
            translations = listOf(
                DString(
                    key = "AuthScreen.Login.Button.Name", value = "Login",
                ),
                DString(
                    key = "AuthScreen.Login.EditTextHint.Hint", value = "Enter your email",
                ))
        )
        val result = task.generateTree(bucket)


        assertEquals(1, result.size)

        val authScreen = result["AuthScreen"]!!
        assertEquals(1, authScreen.children.size)

        val login = authScreen.children["Login"]!!
        assertEquals(2, login.children.size)

        val button = login.children["Button"]!!
        assertEquals(1, button.children.size)

        val hint = login.children["EditTextHint"]!!
        assertEquals(1, button.children.size)

        assertEquals("Login", button.children["Name"]!!.value!!.value)
        assertEquals("Enter your email", hint.children["Hint"]!!.value!!.value)
    }
}