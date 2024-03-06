package com.robotutor.iot.auth.models

import com.robotutor.iot.utils.models.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    USER_ID(10),
    TOKEN_ID(16),
    OTP_ID(12)
}
