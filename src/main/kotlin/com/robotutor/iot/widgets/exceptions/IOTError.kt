package com.robotutor.iot.widgets.exceptions

import com.robotutor.iot.utils.exceptions.ServiceError


enum class IOTError(override val errorCode: String, override val message: String) : ServiceError {
    IOT0501("IOT-0501", "Invoice seed item already exists."),
    IOT0502("IOT-0502", "Invoice seed item not exists."),
    IOT0503("IOT-0503", "Item not in cart."),
}
