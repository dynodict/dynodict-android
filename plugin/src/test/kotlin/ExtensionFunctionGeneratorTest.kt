import org.dynodict.plugin.generation.ExtensionFunctionGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExtensionFunctionGeneratorTest {
    @Test
    fun `WHEN function with 0 parameters THEN no extension method should be returned`() {
        with(ExtensionFunctionGenerator("test.package")) {
            val result = generate(emptyList())
            assertEquals("", result)
        }
    }

    @Test
    fun `WHEN function with 2 parameters THEN named extension method should be returned`() {
        with(ExtensionFunctionGenerator("test.package")) {
            val result = generate(listOf("startDate", "money"))

            println("result:$result")
            assertTrue(result.contains(FEW_PARAMETERS))
        }
    }

    @Test
    fun `WHEN function with more then 3 parameters THEN extension method with map should be returned`() {
        with(ExtensionFunctionGenerator("test.package", paramsThreshold = 2)) {
            val result = generate(listOf("startDate", "money", "paymentDate", "fullDate"))

            println("result:$result")
            assertTrue(result.contains(MANY_PARAMETERS))
        }
    }

    companion object {
        const val FEW_PARAMETERS = """fun DynoDict.Companion.registerFormatters(
    startDateFormatter: DynoDictFormatter<*>,
    moneyFormatter: DynoDictFormatter<*>
) {
    with(DynoDict.instance) {
        registerFormatter("startDate", startDateFormatter)
        registerFormatter("money", moneyFormatter)
    }
}"""


        const val MANY_PARAMETERS = """fun DynoDict.Companion.registerFormatters(formats: Map<String, DynoDictFormatter<*>>) {
    val list = listOf("startDate", "money", "paymentDate", "fullDate")
    val result = list - formats.keys
    if (result.isNotEmpty()) {""" +
                "\n        throw IllegalStateException(\"Not all formatters passed. Please also add remaining: \$result\")\n" +
                """    }
}"""
    }
}