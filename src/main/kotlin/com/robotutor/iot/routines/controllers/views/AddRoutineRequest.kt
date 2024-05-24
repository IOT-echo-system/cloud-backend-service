package com.robotutor.iot.routines.controllers.views

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AddRoutineRequest(
    @field:NotBlank(message = "Routine name should not be blank")
    @field:Size(min = 4, max = 30, message = "Routine name should not be less than 4 char or more than 30 char")
    val name: String,
    val description: String?
)
