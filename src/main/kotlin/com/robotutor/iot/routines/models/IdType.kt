package com.robotutor.iot.routines.models

import com.robotutor.iot.utils.models.IdSequenceType


enum class IdType(override val length: Int) : IdSequenceType {
    ROUTINE_ID(8),
}
