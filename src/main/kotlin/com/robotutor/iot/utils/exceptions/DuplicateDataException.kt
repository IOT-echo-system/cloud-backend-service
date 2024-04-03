package com.robotutor.iot.utils.exceptions

class DuplicateDataException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
