package org.dynodict.plugin.xml.entity

import org.junit.Assert.*
import org.junit.Test

class ResourcesParserTest {

    @Test
    fun `WHEN xml contains string parameter THEN parsed parameter should be String`() {
        val parser = ResourcesParser()
        val parsed = parser.parse(WITH_PARAM)
        assertEquals(1, parsed.size)
        assertEquals("Hello {param0}", parsed[0].value)
    }

    @Test
    fun `WHEN xml contains 2 positional string params THEN order should be preserved and named properly`() {
        val parser = ResourcesParser()
        val strings = parser.parse(TWO_POS_PARAMS)
        assertEquals(1, strings.size)
        assertEquals("{param3}: armed using {param1}", strings[0].value)
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
        assertEquals("This product costs {param0} and you get {param1} discount", strings[0].value)
        assertTrue(strings[0].params[0].format == "%15.8f")
        assertTrue(strings[0].params[1].format == "%.8f")
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
        assertEquals("This product costs {param1}", strings[0].value)
        assertEquals("%15.8f", strings[0].params[0].format )
        assertEquals("float", strings[0].params[0].type)
    }

    companion object {

        private const val WITH_PARAM = """
        <resources>
            <string name="test">Hello %s</string>
        </resources>
    """

        private const val TWO_POS_PARAMS =
            "<resources>" +
                "<string name=\"ACCESS_CARD.Arm.TRIGGERED\">%3\$s: armed using %1\$s</string>\n" +
                "</resources>"

        private const val FLOAT_PRECISION_AND_WIDTH =
            "<resources>" +
                "<string name=\"Info_with_floats\">This product costs %15.8f and you get %.8f discount</string>\n" +
                "</resources>"

        private const val JUST_FLOAT =
            "<resources>" +
                "<string name=\"Info_with_floats\">This product costs %f</string>\n" +
                "</resources>"

        private const val POSITIONAL_FLOAT =
            "<resources>" +
                "<string name=\"Info_with_floats\">This product costs %1$15.8f</string>\n" +
                "</resources>"
    }
}