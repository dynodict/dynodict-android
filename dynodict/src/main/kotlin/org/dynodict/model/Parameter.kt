package org.dynodict.model

sealed class Parameter(val format: String?) {
    class IntParameter(val value: Int, format: String? = null) : Parameter(format)
    class LongParameter(val value: Long, format: String? = null) : Parameter(format)
    class FloatParameter(val value: Float, format: String? = null) : Parameter(format)
    class StringParameter(val value: String, format: String? = null) : Parameter(format)
}

