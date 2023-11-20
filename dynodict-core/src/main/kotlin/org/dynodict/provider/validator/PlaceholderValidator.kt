package org.dynodict.provider.validator

import org.dynodict.RedundantPlaceholderException
import org.dynodict.model.StringKey
import org.dynodict.model.settings.RedundantPlaceholderPolicy
import org.dynodict.model.settings.RedundantPlaceholderPolicy.*
import org.dynodict.model.settings.RedundantPlaceholderPolicy.Nothing
import java.util.regex.Pattern

interface PlaceholderValidator {

    fun validate(key: StringKey, input: String): String
}

class PlaceholderValidatorImpl(val policy: RedundantPlaceholderPolicy) : PlaceholderValidator {

    private val pattern = Pattern.compile(PLACEHOLDER_REGEX)

    override fun validate(key: StringKey, input: String): String {
        if (policy == Nothing) {
            return input
        } else {
            val matcher = pattern.matcher(input)
            if (policy == Remove) {
                return matcher.replaceAll(" ")
                    .trim()
            }
            if (policy == ThrowException) {
                val results = mutableListOf<String>()
                while (matcher.find()) {
                    results.add(matcher.group())
                }
                if (results.isNotEmpty()) {
                    val stringResults = results.joinToString(",")
                    throw RedundantPlaceholderException(
                        "The string contains too many placeholders for key:$key. " +
                            "Redundant placeholders: $stringResults"
                    )
                }
            }
            return input
        }
    }

    companion object {

        const val PLACEHOLDER_REGEX = "( *\\{\\w+\\} *)"
    }

}