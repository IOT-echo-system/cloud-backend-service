package com.robotutor.iot.accounts.controllers.views

import jakarta.validation.constraints.NotBlank

data class AddAccountRequest(
    @field:NotBlank(message = "Project name should not be blank")
    val name: String
)
