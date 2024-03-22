package com.robotutor.iot.accounts.controllers

import com.robotutor.iot.accounts.controllers.views.PolicyView
import com.robotutor.iot.accounts.services.PolicyService
import com.robotutor.iot.accounts.services.RoleService
import com.robotutor.iot.utils.models.UserAuthenticationData
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/policies")
class PolicyController(private val policyService: PolicyService, private val roleService: RoleService) {

    @GetMapping
    fun getPolicies(userAuthenticationData: UserAuthenticationData): Flux<PolicyView> {
        return roleService.getRoleByRoleId(userAuthenticationData.roleId)
            .flatMapMany { role ->
                policyService.getPolicies(role.policies).map { PolicyView.from(it) }
            }
    }
}
