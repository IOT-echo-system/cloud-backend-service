package com.robotutor.iot.widgets.modals

import com.robotutor.iot.utils.models.IdSequenceType


enum class IdType(override val length: Int) : IdSequenceType {
    WIDGET_ID(8),
    BUTTON_ID(10)
}
