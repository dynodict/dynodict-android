package org.dynodict.model

sealed class Parameter(val key: String, val format: String?) {
    class IntParameter(val value: Int, key: String, format: String? = null) : Parameter(key, format)
    class LongParameter(val value: Long, key: String, format: String? = null) :
        Parameter(key, format)

    class FloatParameter(val value: Float, key: String, format: String? = null) :
        Parameter(key, format)

    class StringParameter(val value: String, key: String, format: String? = null) :
        Parameter(key, format)
}

