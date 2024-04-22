package com.robotutor.iot.widgets.gateway

import com.robotutor.iot.webClient.WebClientWrapper
import com.robotutor.iot.widgets.collectionOfButtons.controllers.views.ButtonState
import com.robotutor.iot.widgets.config.NodeBffGatewayConfig
import com.robotutor.iot.widgets.invoice.controllers.views.InvoiceState
import com.robotutor.iot.widgets.modals.Widget
import com.robotutor.iot.widgets.modals.WidgetState
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class NodeBffGateway(
    private val webClientWrapper: WebClientWrapper,
    private val nodeBffGatewayConfig: NodeBffGatewayConfig
) {
    fun updateInvoicePaymentStatus(widget: Widget, invoiceState: InvoiceState): Mono<String> {
        return makeApiRequest(
            path = "/widgets/invoices/{widgetId}/state",
            uriVariables = mutableMapOf(),
            widget = widget,
            widgetState = invoiceState
        )
    }

    fun updateCollectionOfButtonsButtonStatus(widget: Widget, buttonState: ButtonState): Mono<String> {
        return makeApiRequest(
            path = "/widgets/collection-of-buttons/{widgetId}/buttons/{buttonId}/state",
            uriVariables = mutableMapOf("buttonId" to buttonState.buttonId),
            widget = widget,
            widgetState = buttonState
        )
    }

    private fun makeApiRequest(
        path: String,
        uriVariables: MutableMap<String, String>,
        widget: Widget,
        widgetState: WidgetState
    ): Mono<String> {
        uriVariables["widgetId"] = widget.widgetId
        return webClientWrapper.post(
            baseUrl = nodeBffGatewayConfig.baseUrl,
            path = path,
            body = widgetState,
            uriVariables = uriVariables,
            headers = mapOf(
                "boardId" to widget.boardId,
                "widgetName" to widget.widgetType.toString(),
                "widgetId" to widget.widgetId
            ),
            returnType = String::class.java
        )
    }
}
