package com.robotutor.iot.routines.exceptions

import com.robotutor.iot.utils.exceptions.ServiceError


enum class IOTError(override val errorCode: String, override val message: String) : ServiceError {
    IOT0201("IOT-0301", "User, Account, or role is not exits"),
}
