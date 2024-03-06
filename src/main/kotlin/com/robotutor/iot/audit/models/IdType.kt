package com.robotutor.iot.audit.models

import com.robotutor.iot.utils.models.IdSequenceType


enum class IdType(override val length: Int) : IdSequenceType {
    AUDIT_ID(16),
}
