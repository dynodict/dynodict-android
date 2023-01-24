package org.dynodict.formatter

class IntFormatter() : DynoDictFormatter<Int> {
    override fun format(value: Any) = value.toString()
}

class LongFormatter() : DynoDictFormatter<Long> {
    override fun format(value: Any) = value.toString()
}

class FloatFormatter() : DynoDictFormatter<Float> {
    override fun format(value: Any) = value.toString()
}

class StringFormatter() : DynoDictFormatter<String> {
    override fun format(value: Any) = value.toString()
}