package com.robotutor.iot.accounts.controllers.views

import com.robotutor.iot.accounts.models.*

data class AccountView(
    val projectId: AccountId,
    val name: String,
    val roles: List<RoleView>,
    val policies: List<PolicyView>
) {
    companion object {
        fun from(account: Account, roles: List<Role>, policies: List<Policy>): AccountView {
            return AccountView(
                projectId = account.accountId,
                name = account.name,
                roles = roles.map { RoleView.from(it) },
                policies = policies.map { PolicyView.from(it) }
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

