package org.dynodict

import org.dynodict.model.DString

interface DynodictConverter {
    // "Any" because of it can be different converter
    fun convert(value: Any): List<DString>
}