package org.dynodict.plugin.generation

import org.dynodict.remote.model.bucket.RemoteBucket
import org.dynodict.remote.model.bucket.RemoteDString
import org.junit.Assert.assertEquals
import org.junit.Test

class TreeInflaterTest {

    @Test
    fun test() {
        val task = TreeInflater()
        val bucket = RemoteBucket(
            schemeVersion = 1,
            editionVersion = 2,
            name = "login",
            translations = listOf(
                RemoteDString(
                    key = "AuthScreen.Login.Button.Name", value = "Login",
                ),
                RemoteDString(
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