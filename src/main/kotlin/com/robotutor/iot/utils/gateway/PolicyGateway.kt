package com.robotutor.iot.utils.gateway

import com.robotutor.iot.accounts.controllers.PolicyController
import com.robotutor.iot.utils.gateway.views.PolicyView
import com.robotutor.iot.utils.gateway.views.UserAccountPoliciesResponseData
import com.robotutor.iot.utils.models.UserAuthenticationData
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class PolicyGateway(private val policyController: PolicyController) {
    fun getPolicies(userAuthenticationData: UserAuthenticationData): Mono<UserAccountPoliciesResponseData> {
        return policyController.getPolicies(userAuthenticationData = userAuthenticationData)
            .collectList()
            .map { policyViews ->
                UserAccountPoliciesResponseData(
                    policies = policyViews.map { policyView ->
                        PolicyView(name = policyView.name, policyId = policyView.policyId)
                    })
            }
    }
}
