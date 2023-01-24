package org.dynodict

import org.dynodict.DynoDict
import org.dynodict.model.StringKey
import org.dynodict.model.Parameter

object LoginScreen : StringKey("LoginScreen") {
    object ButtonName : StringKey("ButtonName", LoginScreen) {
        fun get(param1: String, param2: Long): String {
            return DynoDict.instance.get(this, Parameter.StringParameter(param1, key = "param1"),Parameter.LongParameter(param2, key = "param2", format = "startDate"))
        }
    }
}