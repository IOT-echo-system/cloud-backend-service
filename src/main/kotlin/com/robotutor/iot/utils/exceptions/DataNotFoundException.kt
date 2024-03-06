package com.robotutor.iot.utils.exceptions

class DataNotFoundException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
