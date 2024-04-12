package com.robotutor.iot.widgets.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.widgets.node-bff")
data class NodeBffGatewayConfig(val baseUrl: String)
