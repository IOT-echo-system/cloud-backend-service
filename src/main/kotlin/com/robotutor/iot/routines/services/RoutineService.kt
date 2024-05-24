package com.robotutor.iot.routines.services

import com.robotutor.iot.logging.logOnError
import com.robotutor.iot.logging.logOnSuccess
import com.robotutor.iot.mqtt.models.AuditEvent
import com.robotutor.iot.mqtt.services.MqttPublisher
import com.robotutor.iot.routines.controllers.views.AddRoutineRequest
import com.robotutor.iot.routines.models.IdType
import com.robotutor.iot.routines.models.Routine
import com.robotutor.iot.routines.models.RoutineId
import com.robotutor.iot.routines.repositories.RoutineRepository
import com.robotutor.iot.utils.audit.auditOnError
import com.robotutor.iot.utils.audit.auditOnSuccess
import com.robotutor.iot.utils.models.UserAuthenticationData
import com.robotutor.iot.utils.services.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Service
class RoutineService(
    private val routineRepository: RoutineRepository,
    private val idGeneratorService: IdGeneratorService,
    private val mqttPublisher: MqttPublisher
) {
    fun addNewRoutine(
        addRoutineRequest: AddRoutineRequest,
        userAuthenticationData: UserAuthenticationData
    ): Mono<Routine> {
        return idGeneratorService.generateId(IdType.ROUTINE_ID)
            .flatMap { routineId ->
                val account = Routine.from(
                    accountId = userAuthenticationData.accountId,
                    addRoutineRequest = addRoutineRequest,
                    userId = userAuthenticationData.userId,
                    routineId = routineId
                )
                val metaData = mapOf("name" to addRoutineRequest.name)
                routineRepository.save(account)
                    .auditOnSuccess(mqttPublisher, AuditEvent.CREATE_ROUTINE, metaData)
                    .auditOnError(mqttPublisher, AuditEvent.CREATE_ROUTINE, metaData)
                    .logOnSuccess(message = "Successfully created new Routine", additionalDetails = metaData)
                    .logOnError(errorMessage = "Failed to create new Routine", additionalDetails = metaData)
            }
    }

    fun getRoutines(accountId: String): Flux<Routine> {
        return routineRepository.findAllByAccountId(accountId)
    }

    fun updateRoutineName(
        name: String,
        routineId: RoutineId,
        userAuthenticationData: UserAuthenticationData
    ): Mono<Routine> {
        return routineRepository.findByRoutineIdAndAccountId(routineId, userAuthenticationData.accountId)
            .map { it.updateName(name) }
            .flatMap { routineRepository.save(it) }
            .auditOnSuccess(mqttPublisher, AuditEvent.UPDATE_ROUTINE_NAME, metadata = mapOf("name" to name))
            .auditOnError(mqttPublisher, AuditEvent.UPDATE_ROUTINE_NAME, metadata = mapOf("name" to name))
            .logOnSuccess(message = "Successfully updated Routine name")
            .logOnError(errorMessage = "Failed to update Routine name")
    }
}
