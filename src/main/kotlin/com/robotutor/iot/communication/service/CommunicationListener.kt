package com.robotutor.iot.communication.service

import com.robotutor.iot.mqtt.models.CommunicationMessage
import com.robotutor.iot.mqtt.models.MqttTopicName
import com.robotutor.iot.mqtt.services.MqttSubscriber
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class CommunicationListener(private val mqttSubscriber: MqttSubscriber, private val mailService: MailService) {

    @PostConstruct
    fun listenForSendMail() {
        mqttSubscriber.subscribe(MqttTopicName.COMMUNICATION, CommunicationMessage::class.java) { message ->
            mailService.sendEmail(message)
        }
    }
}
