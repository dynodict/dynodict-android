package org.dynodict.model

sealed class Parameter(val value: Any, val key: String, val format: String?) {
    class IntParameter(value: Int, key: String, format: String? = null) :
        Parameter(value, key, format)

    class LongParameter(value: Long, key: String, format: String? = null) :
        Parameter(value, key, format)

    class FloatParameter(value: Float, key: String, format: String? = null) :
        Parameter(value, key, format)

    class StringParameter(value: String, key: String, format: String? = null) :
        Parameter(value, key, format)
}

