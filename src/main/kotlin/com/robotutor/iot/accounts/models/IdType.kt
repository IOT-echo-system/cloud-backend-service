package com.robotutor.iot.accounts.models

import com.robotutor.iot.utils.models.IdSequenceType


enum class IdType(override val length: Int) : IdSequenceType {
    ACCOUNT_ID(6),
    ROLE_ID(5),
    POLICY_ID(6),
    BOARD_ID(12),
}
