package com.robotutor.iot.communication.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.communication")
data class CommunicationConfig(val from: String)
