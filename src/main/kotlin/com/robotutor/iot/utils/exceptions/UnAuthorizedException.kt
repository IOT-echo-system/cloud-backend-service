package com.robotutor.iot.utils.exceptions

class UnAuthorizedException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
