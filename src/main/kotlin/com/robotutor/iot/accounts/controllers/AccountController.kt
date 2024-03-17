package com.robotutor.iot.accounts.controllers

import com.robotutor.iot.accounts.controllers.views.AccountValidationRequest
import com.robotutor.iot.accounts.controllers.views.AccountView
import com.robotutor.iot.accounts.controllers.views.AccountWithRoles
import com.robotutor.iot.accounts.controllers.views.AddAccountRequest
import com.robotutor.iot.accounts.services.AccountService
import com.robotutor.iot.accounts.services.PolicyService
import com.robotutor.iot.accounts.services.RoleService
import com.robotutor.iot.utils.models.UserAuthenticationData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/accounts")
class AccountController(
    private val accountService: AccountService,
    private val roleService: RoleService,
    private val policyService: PolicyService
) {

    @GetMapping
    fun getAccounts(userAuthenticationData: UserAuthenticationData): Flux<AccountWithRoles> {
        return accountService.getAccounts(userAuthenticationData.userId)
            .flatMap { account ->
                roleService.getRoles(account.users.first().roles)
                    .map { roles -> AccountWithRoles.from(account, roles) }
            }
    }

    @PostMapping
    fun addNewAccounts(
        @RequestBody @Validated addAccountRequest: AddAccountRequest,
        userAuthenticationData: UserAuthenticationData
    ): Mono<AccountWithRoles> {
        return accountService.addNewAccount(addAccountRequest, userAuthenticationData.userId)
            .flatMap { account ->
                roleService.getRoles(account.users.first().roles)
                    .map { roles -> AccountWithRoles.from(account, roles) }
            }
    }

    @GetMapping("/account-details")
    fun getAccountDetails(userAuthenticationData: UserAuthenticationData): Mono<AccountView> {
        return accountService.getAccountDetails(userAuthenticationData)
            .flatMap { account ->
                roleService.getRoles(account.users.first().roles)
                    .flatMap { roles ->
                        val role = roles.find { it.roleId == userAuthenticationData.roleId }!!
                        policyService.getPolicies(role.policies)
                            .map{ policies ->
                                AccountView.from(account, roles, policies)
                            }
                    }
            }
    }

    @PostMapping("/validate")
    fun isValidAccount(
        @Validated @RequestBody accountValidationRequest: AccountValidationRequest,
        userAuthenticationData: UserAuthenticationData
    ): Mono<Map<String, Boolean>> {
        return accountService.isAccountExistsWithRole(accountValidationRequest, userAuthenticationData.userId)
    }
}
