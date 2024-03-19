package com.robotutor.iot.accounts.services

import com.robotutor.iot.accounts.controllers.views.AccountValidationRequest
import com.robotutor.iot.accounts.controllers.views.AddAccountRequest
import com.robotutor.iot.accounts.exceptions.IOTError
import com.robotutor.iot.accounts.models.Account
import com.robotutor.iot.accounts.models.IdType
import com.robotutor.iot.accounts.repositories.AccountRepository
import com.robotutor.iot.logging.logOnError
import com.robotutor.iot.logging.logOnSuccess
import com.robotutor.iot.mqtt.models.AuditEvent
import com.robotutor.iot.mqtt.services.MqttPublisher
import com.robotutor.iot.utils.audit.auditOnError
import com.robotutor.iot.utils.audit.auditOnSuccess
import com.robotutor.iot.utils.exceptions.DataNotFoundException
import com.robotutor.iot.utils.models.UserAuthenticationData
import com.robotutor.iot.utils.services.IdGeneratorService
import com.robotutor.iot.utils.utils.createMono
import com.robotutor.iot.utils.utils.createMonoError
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val idGeneratorService: IdGeneratorService,
    private val mqttPublisher: MqttPublisher
) {
    fun addNewAccount(addAccountRequest: AddAccountRequest, userId: String): Mono<Account> {
        return idGeneratorService.generateId(IdType.ACCOUNT_ID)
            .flatMap { accountId ->
                val account = Account.from(accountId, addAccountRequest, userId)
                val metaData = mapOf("name" to addAccountRequest.name)
                accountRepository.save(account)
                    .auditOnSuccess(mqttPublisher, AuditEvent.CREATE_ACCOUNT, metaData, accountId = accountId)
                    .auditOnError(mqttPublisher, AuditEvent.CREATE_ACCOUNT, metaData, accountId = accountId)
                    .logOnSuccess(message = "Successfully created new Account", additionalDetails = metaData)
                    .logOnError(errorMessage = "Failed to create new Account", additionalDetails = metaData)
            }
    }

    fun getAccounts(userId: String): Flux<Account> {
        return accountRepository.findAllByUserId(userId)
            .map { account ->
                val users = account.users.find { it.userId == userId }
                account.copy(users = listOf(users!!))
            }
    }

    fun isAccountExistsWithRole(
        accountValidationRequest: AccountValidationRequest,
        userId: String
    ): Mono<Map<String, Boolean>> {
        return accountRepository.findByAccountId(accountValidationRequest.accountId)
            .flatMap { account ->
                val user = account.users.find { it.userId == userId }
                val role = user?.roles?.find { it == accountValidationRequest.roleId }
                if (role.isNullOrBlank()) {
                    createMonoError(DataNotFoundException(IOTError.IOT0201))
                } else {
                    createMono(mapOf("success" to true))
                }
            }
            .switchIfEmpty { createMonoError(DataNotFoundException(IOTError.IOT0201)) }
    }

    fun getAccountDetails(userAuthenticationData: UserAuthenticationData): Mono<Account> {
        return accountRepository.findByAccountId(userAuthenticationData.accountId)
            .map { account ->
                val users = account.users.find { it.userId == userAuthenticationData.userId }
                account.copy(users = listOf(users!!))
            }
    }
}
