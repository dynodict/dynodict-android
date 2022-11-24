package org.dynodict.model

import org.dynodict.FallbackStrategy

data class Settings(val fallbackStrategy: FallbackStrategy){
    companion object{
        val Default = Settings(fallbackStrategy = FallbackStrategy.ReturnDefault)
    }
}

