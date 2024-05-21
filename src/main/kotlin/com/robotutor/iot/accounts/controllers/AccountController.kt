package com.robotutor.iot.accounts.controllers

import com.robotutor.iot.accounts.controllers.views.AccountValidationRequest
import com.robotutor.iot.accounts.controllers.views.AccountView
import com.robotutor.iot.accounts.controllers.views.AddAccountRequest
import com.robotutor.iot.accounts.models.AccountId
import com.robotutor.iot.accounts.services.AccountService
import com.robotutor.iot.utils.filters.annotations.RequirePolicy
import com.robotutor.iot.utils.models.UserAuthenticationData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/accounts")
class AccountController(private val accountService: AccountService) {

    @GetMapping
    fun getAccounts(userAuthenticationData: UserAuthenticationData): Flux<AccountView> {
        return accountService.getAccounts(userAuthenticationData.userId)
            .map { account -> AccountView.from(account, userAuthenticationData) }
    }

    @PostMapping
    fun addNewAccounts(
        @RequestBody @Validated addAccountRequest: AddAccountRequest,
        userAuthenticationData: UserAuthenticationData
    ): Mono<AccountView> {
        return accountService.addNewAccount(addAccountRequest, userAuthenticationData.userId)
            .map { account -> AccountView.from(account, userAuthenticationData) }
    }

    @GetMapping("/account-details")
    fun getAccountDetails(userAuthenticationData: UserAuthenticationData): Mono<AccountView> {
        return accountService.getAccountDetails(userAuthenticationData)
            .map { account -> AccountView.from(account, userAuthenticationData) }
    }

    @PostMapping("/validate")
    fun isValidAccount(
        @Validated @RequestBody accountValidationRequest: AccountValidationRequest,
        userAuthenticationData: UserAuthenticationData
    ): Mono<Map<String, Boolean>> {
        return accountService.isAccountExistsWithRole(accountValidationRequest, userAuthenticationData.userId)
    }

    @RequirePolicy("PROJECT_UPDATE")
    @PutMapping("/{accountId}/name")
    fun updateProjectName(
        @RequestBody @Validated addAccountRequest: AddAccountRequest,
        @PathVariable accountId: AccountId,
        userAuthenticationData: UserAuthenticationData
    ): Mono<AccountView> {
        return accountService.updateAccountName(addAccountRequest.name, userAuthenticationData)
            .map { account -> AccountView.from(account, userAuthenticationData) }
    }
}
