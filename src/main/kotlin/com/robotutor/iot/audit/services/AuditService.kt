package com.robotutor.iot.audit.services

import com.robotutor.iot.audit.models.Audit
import com.robotutor.iot.audit.models.IdType
import com.robotutor.iot.audit.repositories.AuditRepository
import com.robotutor.iot.logging.logOnError
import com.robotutor.iot.logging.logOnSuccess
import com.robotutor.iot.mqtt.models.AuditMessage
import com.robotutor.iot.utils.services.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuditService(private val auditRepository: AuditRepository, private val idGeneratorService: IdGeneratorService) {
    fun addAudit(auditMessage: AuditMessage): Mono<Audit> {
        return idGeneratorService.generateId(IdType.AUDIT_ID)
            .flatMap { auditId ->
                auditRepository.save(Audit.from(auditId, auditMessage))
            }
            .logOnSuccess(message = "Successfully added new audit")
            .logOnError(errorMessage = "Failed to add new audit")
    }
}
