package com.robotutor.iot.widgets.invoice.services

import com.robotutor.iot.logging.logOnError
import com.robotutor.iot.logging.logOnSuccess
import com.robotutor.iot.mqtt.models.AuditEvent
import com.robotutor.iot.mqtt.services.MqttPublisher
import com.robotutor.iot.utils.audit.auditOnError
import com.robotutor.iot.utils.audit.auditOnSuccess
import com.robotutor.iot.utils.models.BoardData
import com.robotutor.iot.utils.services.IdGeneratorService
import com.robotutor.iot.widgets.invoice.modals.Invoice
import com.robotutor.iot.widgets.invoice.repositories.InvoiceRepository
import com.robotutor.iot.widgets.modals.IdType
import com.robotutor.iot.widgets.modals.WidgetType
import com.robotutor.iot.widgets.services.WidgetService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
class InvoiceService(
    private val invoiceRepository: InvoiceRepository,
    private val idGeneratorService: IdGeneratorService,
    private val widgetService: WidgetService,
    private val mqttPublisher: MqttPublisher,
) {
    fun addInvoice(boardData: BoardData): Mono<Invoice> {
        return idGeneratorService.generateId(IdType.INVOICE_ID)
            .flatMap { invoiceId ->
                widgetService.addWidget(
                    foreignWidgetId = invoiceId,
                    accountId = boardData.accountId,
                    boardId = boardData.boardId,
                    widgetType = WidgetType.INVOICE
                )
            }
            .flatMap {
                invoiceRepository.save(Invoice.from(it.widgetId, it.boardId))
            }
            .auditOnSuccess(mqttPublisher, AuditEvent.ADD_WIDGET)
            .auditOnError(mqttPublisher, AuditEvent.ADD_WIDGET)
            .logOnSuccess(message = "Successfully added widget")
            .logOnError(errorMessage = "Failed to add widget")
    }
}
