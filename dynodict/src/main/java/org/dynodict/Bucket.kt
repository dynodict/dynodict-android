package org.dynodict

interface Bucket : DString {
    val strings: List<DString>
}