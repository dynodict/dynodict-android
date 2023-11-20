package org.dynodict

import org.dynodict.model.StringKey
import org.dynodict.model.settings.RedundantPlaceholderPolicy
import org.dynodict.provider.validator.PlaceholderValidatorImpl
import org.junit.Assert.*
import org.junit.Test

class PlaceholderValidatorImplTest {

    @Test
    fun `WHEN policy is ThrowException and input doesn't have any placeholder THEN output should be same as input`() {
        val input = "String without any placeholder"
        with(PlaceholderValidatorImpl(policy = RedundantPlaceholderPolicy.ThrowException)) {
            assertEquals(input, validate(StringKey("Key"), input))
        }
    }

    @Test
    fun `WHEN policy is ThrowException and input has placeholder THEN exception should be thrown`() {
        val input = "String with {placeholder1}"
        with(PlaceholderValidatorImpl(policy = RedundantPlaceholderPolicy.ThrowException)) {
            try {
                validate(StringKey("StringKey"), input)
                fail()
            } catch (ex: RedundantPlaceholderException) {
                // expected
                assertTrue(ex.message!!.contains("StringKey"))
                assertTrue(ex.message!!.contains("placeholder1"))
            }
        }
    }


    @Test
    fun `WHEN policy is Nothing and input has placeholder THEN output should be same as input`() {
        val input = "String with {placeholder}"
        with(PlaceholderValidatorImpl(policy = RedundantPlaceholderPolicy.Nothing)) {
            assertEquals(input, validate(StringKey("Key"), input))
        }
    }

    @Test
    fun `WHEN policy is Nothing and input doesn't have placeholder THEN output should be same as input`() {
        val input = "String without placeholder"
        with(PlaceholderValidatorImpl(policy = RedundantPlaceholderPolicy.Nothing)) {
            assertEquals(input, validate(StringKey("Key"), input))
        }
    }

    @Test
    fun `WHEN policy is Remove and input doesn't have placeholder THEN output should be same as input`() {
        val input = "String without placeholder"
        with(PlaceholderValidatorImpl(policy = RedundantPlaceholderPolicy.Remove)) {
            assertEquals(input, validate(StringKey("Key"), input))
        }
    }

    @Test
    fun `WHEN policy is Remove and input contains placeholder THEN output shouldn't have placeholders `() {
        val input = "String with {placeholder} and some text"
        with(PlaceholderValidatorImpl(policy = RedundantPlaceholderPolicy.Remove)) {
            assertEquals("String with and some text", validate(StringKey("Key"), input))
        }
    }

    @Test
    fun `WHEN policy is Remove and input contains placeholder at the end THEN output shouldn't have placeholders `() {
        val input = "String with and some text {placeholder}"
        with(PlaceholderValidatorImpl(policy = RedundantPlaceholderPolicy.Remove)) {
            assertEquals("String with and some text", validate(StringKey("Key"), input))
        }
    }

    @Test
    fun `WHEN policy is Remove and input contains placeholder at the start THEN output shouldn't have placeholders `() {
        val input = "{placeholder} String with and some text"
        with(PlaceholderValidatorImpl(policy = RedundantPlaceholderPolicy.Remove)) {
            assertEquals("String with and some text", validate(StringKey("Key"), input))
        }
    }

}