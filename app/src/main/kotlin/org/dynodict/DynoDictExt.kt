package org.dynodict

import org.dynodict.formatter.DynoDictFormatter

fun DynoDict.Companion.registerFormatters(
    startDateFormatter: DynoDictFormatter<*>
) {
    with(instance) {
        registerFormatter("startDate", startDateFormatter)
    }
}
