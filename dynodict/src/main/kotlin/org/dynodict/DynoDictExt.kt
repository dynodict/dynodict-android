import org.dynodict.DynoDict
import org.dynodict.formatter.DynoDictFormatter

fun DynoDict.registerFormatters(formats: Map<String, DynoDictFormatter<*>>) {
    val list = listOf("startDate", "money", "paymentDate", "fullDate")
    val result = list - formats.keys
    if (result.isNotEmpty()) {
        throw IllegalStateException("Not all formatters passed. Please also add remaining: $result")
    }
}