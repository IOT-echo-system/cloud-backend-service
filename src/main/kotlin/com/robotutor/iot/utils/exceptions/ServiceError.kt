package com.robotutor.iot.utils.exceptions

interface ServiceError {
    val errorCode: String
    val message: String
}

fun ServiceError.toBaseException() = BaseException(this)
