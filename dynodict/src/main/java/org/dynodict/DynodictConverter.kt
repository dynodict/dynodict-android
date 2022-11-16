package org.dynodict

interface DynodictConverter {
    // "Any" because of it can be different converter
    fun convert(value: Any): List<DString>
}