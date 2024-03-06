package com.robotutor.iot.mqtt.services

import com.robotutor.iot.logging.LogDetails
import com.robotutor.iot.logging.Logger
import com.robotutor.iot.logging.serializer.DefaultSerializer
import com.robotutor.iot.mqtt.models.Message
import com.robotutor.iot.mqtt.models.MqttTopicName
import org.springframework.stereotype.Service

@Service
class MqttSubscriber(private val mqttClientService: MqttClientService) {
    val logger = Logger(this::class.java)
    fun <T : Message> subscribe(topicName: MqttTopicName, messageType: Class<T>, handler: (T) -> Unit) {
        val mqttClient = mqttClientService.connect()
        mqttClient.subscribe(topicName.toString(), 1) { _, msg ->
            try {
                handler(DefaultSerializer.deserialize(msg.toString(), messageType))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        logger.info(LogDetails(message = "Successfully subscribed topic with $topicName"))
    }
}
