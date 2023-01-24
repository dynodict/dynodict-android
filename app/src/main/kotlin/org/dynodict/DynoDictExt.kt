package org.dynodict

import org.dynodict.formatter.DynoDictFormatter
import org.dynodict.DynoDict

fun DynoDict.Companion.registerFormatters(
    startDateFormatter: DynoDictFormatter<*>
) {
    with(DynoDict.instance) {
        registerFormatter("startDate", startDateFormatter)
    }
}
