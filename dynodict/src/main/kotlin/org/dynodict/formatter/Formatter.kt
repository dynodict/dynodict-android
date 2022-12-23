package org.dynodict.org.dynodict.formatter

interface Formatter<T> {
    fun format(value: Any): String
}

class IntFormatter() : Formatter<Int> {
    override fun format(value: Any) = value.toString()
}

class LongFormatter() : Formatter<Long> {
    override fun format(value: Any) = value.toString()
}

class FloatFormatter() : Formatter<Float> {
    override fun format(value: Any) = value.toString()
}

class StringFormatter() : Formatter<String> {
    override fun format(value: Any) = value.toString()
}