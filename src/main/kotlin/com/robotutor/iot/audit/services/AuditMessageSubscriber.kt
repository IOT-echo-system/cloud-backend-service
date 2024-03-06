package com.robotutor.iot.audit.services

import com.robotutor.iot.mqtt.models.MqttTopicName
import com.robotutor.iot.mqtt.services.MqttSubscriber
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import com.robotutor.iot.mqtt.models.AuditMessage

@Service
class AuditMessageSubscriber(
    private val auditService: AuditService,
    private val mqttSubscriber: MqttSubscriber,
) {
    @PostConstruct
    fun subscribe() {
        mqttSubscriber.subscribe(MqttTopicName.AUDIT, AuditMessage::class.java) { msg ->
            auditService.addAudit(msg).subscribe()
        }
    }
}
