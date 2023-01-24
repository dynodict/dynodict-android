package org.dynodict.model.settings

import org.dynodict.org.dynodict.model.settings.ParameterFallbackStrategy

data class Settings(
    val fallbackStrategy: FallbackStrategy,
    val parameterStrategy: ParameterFallbackStrategy
) {
    companion object {
        val Default = Settings(
            fallbackStrategy = FallbackStrategy.ReturnDefault,
            parameterStrategy = ParameterFallbackStrategy.ReplaceByEmptyString
        )

        val Strict = Settings(
            fallbackStrategy = FallbackStrategy.ThrowException,
            parameterStrategy = ParameterFallbackStrategy.ThrowException
        )
        val Production = Settings(
            fallbackStrategy = FallbackStrategy.ReturnDefault,
            parameterStrategy = ParameterFallbackStrategy.ReplaceByEmptyString
        )
    }
}

