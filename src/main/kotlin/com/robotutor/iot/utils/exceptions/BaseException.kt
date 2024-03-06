package com.robotutor.iot.utils.exceptions


open class BaseException(
    private val errorCode: String,
    override val message: String,
    private var details: Map<String, Any> = emptyMap(),
    override val cause: Throwable? = null
) : Throwable(message = message, cause = cause) {
    constructor(serviceError: ServiceError, details: Map<String, Any> = emptyMap(), cause: Throwable? = null) : this(
        serviceError.errorCode,
        serviceError.message,
        details,
        cause
    )

    fun errorResponse(): Map<String, String> {
        return mapOf(
            "errorCode" to errorCode,
            "message" to message,
        )
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "errorCode" to errorCode,
            "message" to message,
            "details" to details
        )
    }
}
