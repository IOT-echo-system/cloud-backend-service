package com.robotutor.iot.accounts.controllers.views

import com.robotutor.iot.accounts.models.Account
import com.robotutor.iot.accounts.models.AccountId
import com.robotutor.iot.accounts.models.Role
import com.robotutor.iot.accounts.models.RoleId

data class AccountWithRoles(val accountId: AccountId, val name: String, val roles: List<RoleView>) {
    companion object {
        fun from(account: Account, roles: List<Role>): AccountWithRoles {
            return AccountWithRoles(
                accountId = account.accountId,
                name = account.name,
                roles = roles.map { role ->
                    RoleView(roleId = role.roleId, name = role.name)
                }
            )
        }
    }
}

data class RoleView(val roleId: RoleId, val name: String)
