package com.robotutor.iot.auth.gateway

import com.robotutor.iot.accounts.controllers.AccountController
import com.robotutor.iot.accounts.controllers.views.AccountValidationRequest
import com.robotutor.iot.auth.gateway.views.ValidateAccountResponse
import com.robotutor.iot.utils.models.UserAuthenticationData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AccountServiceGateway(
    private val accountController: AccountController
) {
    fun isValidAccountAndRole(userId: String, accountId: String, roleId: String): Mono<ValidateAccountResponse> {
        return accountController.isValidAccount(
            accountValidationRequest = AccountValidationRequest(accountId = accountId, roleId = roleId),
            userAuthenticationData = UserAuthenticationData(userId = userId, accountId = accountId, roleId = roleId)
        )
            .map {
                ValidateAccountResponse(true)
            }
    }
}
