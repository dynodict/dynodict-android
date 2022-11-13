package org.dynodict

data class Settings(val fallbackStrategy: FallbackStrategy){
    companion object{
        val Default = Settings(fallbackStrategy = FallbackStrategy.ReturnDefault)
    }
}

enum class FallbackStrategy {
    ThrowException, EmptyString, ReturnDefault
}