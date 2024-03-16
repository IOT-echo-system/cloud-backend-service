package com.robotutor.iot.utils.exceptions

enum class IOTError(override val errorCode: String, override val message: String) : ServiceError {
    IOT0101("IOT-0101", "Unauthorized user."),
    IOT0102("IOT-0102", "Unauthorized user."),
}
