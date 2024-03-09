package com.robotutor.iot.auth.gateway

import com.robotutor.iot.auth.config.AccountGatewayConfig
import com.robotutor.iot.auth.gateway.views.ValidateAccountResponse
import com.robotutor.iot.webClient.WebClientWrapper
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AccountServiceGateway(
    private val webClientWrapper: WebClientWrapper,
    private val accountGatewayConfig: AccountGatewayConfig
) {
    fun isValidAccountAndRole(userId: String, accountId: String, roleId: String): Mono<ValidateAccountResponse> {
        return webClientWrapper.post(
            baseUrl = accountGatewayConfig.baseUrl,
            path = accountGatewayConfig.validateRoleAndAccountPath,
            returnType = ValidateAccountResponse::class.java,
            body = mapOf("userId" to userId, "accountId" to accountId, "roleId" to roleId)
        )
    }
}
