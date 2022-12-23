package org.dynodict.model.settings

data class Settings(val fallbackStrategy: FallbackStrategy){
    companion object{
        val Default = Settings(fallbackStrategy = FallbackStrategy.ReturnDefault)
    }
}
