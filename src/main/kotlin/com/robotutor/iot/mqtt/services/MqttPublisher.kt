package com.robotutor.iot.mqtt.services

import com.robotutor.iot.logging.LogDetails
import com.robotutor.iot.logging.Logger
import com.robotutor.iot.logging.serializer.DefaultSerializer
import com.robotutor.iot.mqtt.models.Message
import com.robotutor.iot.mqtt.models.MqttTopicName
import org.springframework.stereotype.Service

@Service
class MqttPublisher(private val mqttClientService: MqttClientService) {
    val logger = Logger(this::class.java)
    fun publish(topicName: MqttTopicName, message: Message) {
        val mqttClient = mqttClientService.connect()
        val payload = DefaultSerializer.serialize(message).toByteArray()
        mqttClient.publish(topicName.toString(), payload, 1, false)
        logger.info(LogDetails(message = "Successfully subscribed topic with $topicName"))
    }
}
