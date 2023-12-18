package org.dynodict.plugin.xml.entity

import org.junit.Test

class ResourcesParserTest {

    @Test
    fun `parse xml`() {
        val parser = ResourcesParser()
        val strings = parser.parse(WITH_PARAM)
        println(strings)
    }

    @Test
    fun `parse xml with 2 params`() {
        val parser = ResourcesParser()
        val strings = parser.parse(WITH_TWO_PARAM)
        println(strings)
    }

    companion object {

        private const val WITH_PARAM = """
        <resources>
            <string name="test">Hello %s</string>
        </resources>
    """

        private const val WITH_TWO_PARAM =
        "<resources>"+
        "<string name=\"ACCESS_CARD.Arm.TRIGGERED\">%3\$s: armed using %1\$s</string>\n"+
        "<string name=\"ACCESS_CARD.ArmAttempt.TRIGGERED\">%3\$s: unsuccessful arming attempt using %1\$s</string>" +
        "</resources>"
    }
}