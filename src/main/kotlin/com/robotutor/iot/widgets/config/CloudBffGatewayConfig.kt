package com.robotutor.iot.widgets.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.widgets.cloud-bff")
data class CloudBffGatewayConfig(val baseUrl: String)
