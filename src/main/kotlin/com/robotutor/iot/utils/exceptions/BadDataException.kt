package com.robotutor.iot.utils.exceptions

class BadDataException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
