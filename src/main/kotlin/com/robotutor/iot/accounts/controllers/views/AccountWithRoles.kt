package com.robotutor.iot.accounts.controllers.views

import com.robotutor.iot.accounts.models.Account
import com.robotutor.iot.accounts.models.AccountId
import com.robotutor.iot.accounts.models.Role

data class AccountWithRoles(val projectId: AccountId, val name: String, val roles: List<RoleView>) {
    companion object {
        fun from(account: Account, roles: List<Role>): AccountWithRoles {
            return AccountWithRoles(
                projectId = account.accountId,
                name = account.name,
                roles = roles.map { role ->
                    RoleView(roleId = role.roleId, name = role.name)
                }
            )
        }
    }
}

