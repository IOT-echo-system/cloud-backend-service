package com.robotutor.iot.accounts.controllers.views

import com.robotutor.iot.accounts.models.AccountId
import com.robotutor.iot.accounts.models.RoleId
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AddAccountRequest(
    @field:NotBlank(message = "Project name should not be blank")
    @field:Size(min = 4, max = 30, message = "Project name should not be less than 4 char or more than 30 char")
    val name: String
)

data class AccountValidationRequest(
    @field:NotBlank(message = "AccountId should not be blank")
    val accountId: AccountId,
    @field:NotBlank(message = "RoleId should not be blank")
    val roleId: RoleId
)
