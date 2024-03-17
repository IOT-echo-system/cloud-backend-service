package com.robotutor.iot.accounts.models

import com.robotutor.iot.utils.models.IdSequenceType


enum class IdType(override val length: Int) : IdSequenceType {
    ACCOUNT_ID(10),
    ROLE_ID(5),
    POLICY_ID(4),
}
