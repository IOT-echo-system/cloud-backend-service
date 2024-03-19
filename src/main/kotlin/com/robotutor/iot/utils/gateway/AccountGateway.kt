package com.robotutor.iot.utils.gateway

import com.robotutor.iot.accounts.controllers.AccountController
import com.robotutor.iot.utils.gateway.views.PolicyView
import com.robotutor.iot.utils.gateway.views.UserAccountPoliciesResponseData
import com.robotutor.iot.utils.models.UserAuthenticationData
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AccountGateway(private val accountController: AccountController) {
    fun getPolicies(userAuthenticationData: UserAuthenticationData): Mono<UserAccountPoliciesResponseData> {
        return accountController.getAccountDetails(userAuthenticationData = userAuthenticationData)
            .map {
                UserAccountPoliciesResponseData(it.policies.map { policy ->
                    PolicyView(
                        policyId = policy.policyId,
                        name = policy.name
                    )
                })
            }
    }
}
