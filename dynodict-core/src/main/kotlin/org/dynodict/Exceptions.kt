package org.dynodict

class DefaultMetadataNotFoundException(message: String, ex: Throwable? = null) :
    Exception(message, ex)

class MetadataNotFoundException(message: String, ex: Throwable? = null) :
    Exception(message, ex)

class StringNotFoundException(message: String, ex: Throwable? = null) : Exception(message, ex)

class DefaultStringNotFoundException(message: String, ex: Throwable? = null) :
    Exception(message, ex)

class FormatterNotFoundException(message: String, ex: Throwable? = null) : Exception(message, ex)

class PlaceholderNotFoundException(message: String, ex: Throwable? = null) : Exception(message, ex)

class RedundantPlaceholderException(message: String, ex: Throwable? = null) : Exception(message, ex)

class LocaleNotFoundException(message: String, ex: Throwable? = null) : Exception(message, ex)

class EndpointNotSetException(message: String, ex: Throwable? = null) : Exception(message, ex)