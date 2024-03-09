package com.robotutor.iot.accounts.controllers.views

import com.robotutor.iot.accounts.models.AccountId
import com.robotutor.iot.accounts.models.RoleId
import jakarta.validation.constraints.NotBlank

data class AddAccountRequest(
    @field:NotBlank(message = "Project name should not be blank")
    val name: String
)

data class AccountValidationRequest(
    @field:NotBlank(message = "AccountId should not be blank")
    val accountId: AccountId,
    @field:NotBlank(message = "RoleId should not be blank")
    val roleId: RoleId
)
