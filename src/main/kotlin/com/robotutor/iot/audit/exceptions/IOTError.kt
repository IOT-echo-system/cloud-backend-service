package com.robotutor.iot.audit.exceptions

import com.robotutor.iot.utils.exceptions.ServiceError


enum class IOTError(override val errorCode: String, override val message: String) : ServiceError {
//    IOT0201("IOT-0201", "User already registered with this email."),
}
