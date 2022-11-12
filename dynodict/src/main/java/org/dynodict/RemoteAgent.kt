package org.dynodict

interface RemoteAgent {
    suspend fun downloadMetadata(): RequestResult<Metadata>
    suspend fun downloadTranslations(infos: List<BucketInfo>): RequestResult<List<Translation>>
}

sealed class RequestResult<T>() {
    data class Success<T>(val result: T) : RequestResult<T>()
    data class Failure<T>(val error: Exception) : RequestResult<T>()
}