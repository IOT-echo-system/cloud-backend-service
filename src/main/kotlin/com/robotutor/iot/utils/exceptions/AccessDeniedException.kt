package com.robotutor.iot.utils.exceptions

class AccessDeniedException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
