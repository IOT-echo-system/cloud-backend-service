package com.robotutor.iot.widgets.collectionOfButtons.controllers.views

import com.robotutor.iot.widgets.collectionOfButtons.modals.ButtonMode
import com.robotutor.iot.widgets.collectionOfButtons.modals.ButtonType
import jakarta.validation.constraints.Size

data class AddButtonRequest(
    @field:Size(min = 4, max = 30, message = "Button name should not be less than 4 char or more than 30 char")
    val name: String,
    val type: ButtonType,
    val mode: ButtonMode,
    val min: Int = 0,
    val max: Int = 1
)
