import org.dynodict.plugin.DownloadStringsTask
import org.dynodict.plugin.generation.StringModel
import org.dynodict.plugin.generation.TreeInflater
import org.dynodict.plugin.remote.Bucket
import org.dynodict.plugin.remote.DString
import org.junit.Assert
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
            language = "En",
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

        val authScreen = (result["AuthScreen"] as StringModel.Container)
        assertEquals(1, authScreen.children.size)

        val login = (authScreen.children["Login"] as StringModel.Container)
        assertEquals(2, login.children.size)

        val button = (login.children["Button"] as StringModel.Container)
        assertEquals(1, button.children.size)

        val hint = (login.children["EditTextHint"] as StringModel.Container)
        assertEquals(1, button.children.size)

        assertEquals("Login", (button.children["Name"] as StringModel.Leaf).value.value)
        assertEquals("Enter your email", (hint.children["Hint"] as StringModel.Leaf).value.value)
    }
}