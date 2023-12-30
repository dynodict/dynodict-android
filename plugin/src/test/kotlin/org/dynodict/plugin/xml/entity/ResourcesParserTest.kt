package org.dynodict.plugin.xml.entity

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ResourcesParserTest {

    @Test
    fun `WHEN xml contains string parameter THEN parsed parameter should be String`() {
        val parser = ResourcesParser()
        val parsed = parser.parse(WITH_PARAM)
        assertEquals(2, parsed.size)
        assertEquals("Hello {param0}", parsed[0].value)
        assertEquals("Hello {param0}", parsed[1].value)
    }

    @Test
    fun `WHEN xml contains 2 positional string params THEN order should be preserved and named properly`() {
        val parser = ResourcesParser()
        val strings = parser.parse(TWO_POS_PARAMS)
        assertEquals(1, strings.size)
        assertEquals("{param3}: test {param1}", strings[0].value)
        assertTrue(strings[0].params[0].format == null)
        assertTrue(strings[0].params[1].format == null)
        assertEquals("string", strings[0].params[0].type)
        assertEquals("string", strings[0].params[1].type)
    }

    @Test
    fun `WHEN xml contains float parameter THEN type should be float`() {
        val parser = ResourcesParser()
        val strings = parser.parse(FLOAT_PRECISION_AND_WIDTH)
        assertEquals(1, strings.size)
        assertEquals(
            "This product costs {param0} and you get {param1} discount only for {param2}$",
            strings[0].value
        )
        assertTrue(strings[0].params[0].format == "%15.8f")
        assertTrue(strings[0].params[1].format == "%.8f")
        assertTrue(strings[0].params[2].format == "%3f")
        assertEquals("float", strings[0].params[0].type)
        assertEquals("float", strings[0].params[1].type)
    }

    @Test
    fun `WHEN xml contains empty float parameter THEN type should be float`() {
        val parser = ResourcesParser()
        val strings = parser.parse(JUST_FLOAT)
        assertEquals(1, strings.size)
        assertEquals("This product costs {param0}", strings[0].value)
        assertTrue(strings[0].params[0].format == null)
        assertEquals("float", strings[0].params[0].type)
    }

    @Test
    fun `WHEN xml contains positional and precision parameter THEN type should be float but with custom`() {
        val parser = ResourcesParser()
        val strings = parser.parse(POSITIONAL_FLOAT)
        assertEquals(1, strings.size)
        assertEquals(
            "This product costs {param1} but you get discount {param0}. The final price is {param2}",
            strings[0].value
        )
        assertEquals("%15.8f", strings[0].params[0].format)
        assertEquals("float", strings[0].params[0].type)
    }

    @Test
    fun `WHEN xml contains int parameter THEN type should be Int`() {
        val parser = ResourcesParser()
        val strings = parser.parse(WITH_INT_PARAM)
        assertEquals(1, strings.size)
        with(strings[0]) {
            assertEquals("{param0} plus {param2} = {param1} ", value)
            assertEquals(null, params[0].format)
            assertEquals("%4d", params[1].format)
            assertEquals("%3d", params[2].format)
            assertEquals("integer", params[0].type)
            assertEquals("integer", params[1].type)
            assertEquals("integer", params[2].type)
        }
    }

    @Test
    fun `WHEN xml contains char parameter THEN type should be Char`() {
        val parser = ResourcesParser()
        val strings = parser.parse(WITH_CHAR_PARAM)
        assertEquals(1, strings.size)
        with(strings[0]) {
            assertEquals("{param0} plus {param2} = {param1} ", value)
            assertEquals(null, params[0].format)
            assertEquals("%4c", params[1].format)
            assertEquals("%3c", params[2].format)
            assertEquals("string", params[0].type)
            assertEquals("string", params[1].type)
            assertEquals("string", params[2].type)
        }
    }

    @Test
    fun `WHEN xml contains non-translatable string THEN is should be ignored`() {
        val parser = ResourcesParser()
        val strings = parser.parse(NON_TRANSLATABLE)
        assertEquals(0, strings.size)
    }
    
    @Test
    fun `WHEN xml contains custom formatting THEN it should be parsed`() {
        val parser = ResourcesParser()
        val strings = parser.parse(WITH_CUSTOM_FORMATTING)
        assertEquals(1, strings.size)
        with(strings[0]) {
            assertEquals("{param0} plus {param2} = {param1} ", value)
            assertEquals("%g", params[0].format)
            assertEquals("%4G", params[1].format)
            assertEquals("%3g", params[2].format)
            assertEquals("string", params[0].type)
            assertEquals("string", params[1].type)
            assertEquals("string", params[2].type)
        }
    }

    companion object {

        private const val WITH_PARAM = """
<resources>
    <string name="test">Hello %s</string>
    <string name="test">Hello %S</string>
</resources>
    """

        private const val TWO_POS_PARAMS =
            "<resources>" +
                    "<string name=\"Test\">%3\$s: test %1\$s</string>\n" +
                    "</resources>"

        private const val FLOAT_PRECISION_AND_WIDTH =
            "<resources>" +
                    "<string name=\"Info_with_floats\">This product costs %15.8f and you get %.8f discount only for %3f$</string>\n" +
                    "</resources>"

        private const val JUST_FLOAT =
            "<resources>" +
                    "<string name=\"Info_with_floats\">This product costs %f</string>\n" +
                    "</resources>"

        private const val POSITIONAL_FLOAT =
            "<resources>" +
                    "<string name=\"Info_with_floats\">This product costs %1$15.8f but you get discount %.2f. The final price is %.2f</string>\n" +
                    "</resources>"

        private const val WITH_INT_PARAM = """
    <resources>
        <string name="test">%d plus %2$4d = %1$3d </string>
    </resources>
    """

        private const val WITH_CHAR_PARAM = """
    <resources>
        <string name="test">%c plus %2$4c = %1$3c </string>
    </resources>
    """
        private const val NON_TRANSLATABLE = """
    <resources>
        <string name="test" translatable="false">%c plus %2$4c = %1$3c </string>
    </resources>
    """

        private const val WITH_CUSTOM_FORMATTING = """
    <resources>
        <string name="test">%g plus %2$4G = %1$3g </string>
    </resources>
    """
    }
}
