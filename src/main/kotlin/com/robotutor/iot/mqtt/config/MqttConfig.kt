package com.robotutor.iot.mqtt.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.mqtt")
data class MqttConfig(
    val broker: String,
    val clientId: String,
    val username: String,
    val password: String
)
