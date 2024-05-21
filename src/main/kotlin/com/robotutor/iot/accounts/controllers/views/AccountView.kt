package com.robotutor.iot.accounts.controllers.views

import com.robotutor.iot.accounts.models.*
import com.robotutor.iot.utils.models.UserAuthenticationData

data class AccountView(
    val projectId: AccountId,
    val name: String,
    val user: User,
) {
    companion object {
        fun from(account: Account, userAuthenticationData: UserAuthenticationData): AccountView {
            return AccountView(
                projectId = account.accountId,
                name = account.name,
                user = account.users.find { it.userId == userAuthenticationData.userId }!!,
            )
        }
    }
}

data class RoleView(val roleId: RoleId, val name: String) {
    companion object {
        fun from(role: Role): RoleView {
            return RoleView(roleId = role.roleId, name = role.name)
        }
    }
}

data class PolicyView(val policyId: PolicyId, val name: String) {
    companion object {
        fun from(policy: Policy): PolicyView {
            return PolicyView(policyId = policy.policyId, name = policy.name)
        }
    }
}

