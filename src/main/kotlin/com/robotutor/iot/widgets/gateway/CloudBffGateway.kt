package com.robotutor.iot.widgets.gateway

import com.robotutor.iot.webClient.WebClientWrapper
import com.robotutor.iot.widgets.config.CloudBffGatewayConfig
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CloudBffGateway(
    private val webClientWrapper: WebClientWrapper,
    private val cloudBffGatewayConfig: CloudBffGatewayConfig
) {
    fun updateWidget(widget: Any): Mono<String> {
        return makeApiRequest(
            path = "/widgets/update-widget",
            uriVariables = mutableMapOf(),
            widget = widget,
        )
    }

    private fun makeApiRequest(
        path: String,
        uriVariables: MutableMap<String, String>,
        widget: Any,
    ): Mono<String> {
        return webClientWrapper.post(
            baseUrl = cloudBffGatewayConfig.baseUrl,
            path = path,
            body = widget,
            uriVariables = uriVariables,
            returnType = String::class.java
        )
    }
}
