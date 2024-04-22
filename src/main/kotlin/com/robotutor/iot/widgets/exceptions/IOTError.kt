package com.robotutor.iot.widgets.exceptions

import com.robotutor.iot.utils.exceptions.ServiceError


enum class IOTError(override val errorCode: String, override val message: String) : ServiceError {
    IOT0501("IOT-0501", "Invoice seed item already exists."),
    IOT0502("IOT-0502", "Invoice seed item not exists."),
    IOT0503("IOT-0503", "Item not in cart."),
    IOT0504("IOT-0504", "Already paid\nCan't add."),
    IOT0505("IOT-0505", "Already paid\nCan't remove."),
    IOT0506("IOT-0506", "Button not exists."),
}
