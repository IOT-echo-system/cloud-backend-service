package com.robotutor.iot.accounts.services

import com.robotutor.iot.accounts.controllers.views.AddAccountRequest
import com.robotutor.iot.accounts.models.Account
import com.robotutor.iot.accounts.models.IdType
import com.robotutor.iot.accounts.repositories.AccountRepository
import com.robotutor.iot.logging.logOnError
import com.robotutor.iot.logging.logOnSuccess
import com.robotutor.iot.mqtt.models.AuditEvent
import com.robotutor.iot.mqtt.services.MqttPublisher
import com.robotutor.iot.utils.audit.auditOnError
import com.robotutor.iot.utils.audit.auditOnSuccess
import com.robotutor.iot.utils.services.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

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
                    .logOnError(errorMessage = "Successfully created new Account", additionalDetails = metaData)
            }
    }

    fun getAccounts(userId: String): Flux<Account> {
        return accountRepository.findAllByUserId(userId)
            .map { account ->
                val users = account.users.find { it.userId == userId }
                account.copy(users = listOf(users!!))
            }
    }
}
