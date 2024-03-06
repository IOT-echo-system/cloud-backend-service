package com.robotutor.iot.audit.models

import com.robotutor.iot.mqtt.models.AuditEvent
import com.robotutor.iot.mqtt.models.AuditMessage
import com.robotutor.iot.mqtt.models.AuditStatus
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

const val AUDIT_COLLECTION = "audit"

@TypeAlias("Audit")
@Document(AUDIT_COLLECTION)
data class Audit(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val auditId: AuditId,
    val status: AuditStatus,
    val userId: String,
    val accountId: String?,
    val deviceId: String?,
    val metadata: Map<String, Any>,
    val event: AuditEvent,
    val timestamp: LocalDateTime
) {
    companion object {
        fun from(auditId: String, message: AuditMessage): Audit {
            return Audit(
                auditId = auditId,
                status = message.status,
                userId = message.userId,
                metadata = message.metadata,
                event = message.event,
                timestamp = message.timestamp,
                accountId = message.accountId,
                deviceId = message.deviceId,
            )
        }
    }
}

typealias AuditId = String
