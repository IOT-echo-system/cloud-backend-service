package com.robotutor.iot.widgets.invoice.services

import com.robotutor.iot.logging.logOnError
import com.robotutor.iot.logging.logOnSuccess
import com.robotutor.iot.mqtt.models.AuditEvent
import com.robotutor.iot.mqtt.services.MqttPublisher
import com.robotutor.iot.utils.audit.auditOnError
import com.robotutor.iot.utils.audit.auditOnSuccess
import com.robotutor.iot.utils.models.BoardAuthenticationData
import com.robotutor.iot.utils.models.UserAuthenticationData
import com.robotutor.iot.utils.models.UserBoardAuthenticationData
import com.robotutor.iot.utils.services.IdGeneratorService
import com.robotutor.iot.widgets.gateway.NodeBffGateway
import com.robotutor.iot.widgets.invoice.controllers.views.InvoiceState
import com.robotutor.iot.widgets.invoice.controllers.views.PaymentRequestBody
import com.robotutor.iot.widgets.invoice.controllers.views.SeedItemRequest
import com.robotutor.iot.widgets.invoice.modals.Invoice
import com.robotutor.iot.widgets.invoice.modals.InvoiceSeedItem
import com.robotutor.iot.widgets.invoice.modals.InvoiceWithError
import com.robotutor.iot.widgets.invoice.repositories.InvoiceRepository
import com.robotutor.iot.widgets.modals.IdType
import com.robotutor.iot.widgets.modals.Widget
import com.robotutor.iot.widgets.modals.WidgetId
import com.robotutor.iot.widgets.modals.WidgetType
import com.robotutor.iot.widgets.services.WidgetService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Service
class InvoiceService(
    private val invoiceRepository: InvoiceRepository,
    private val idGeneratorService: IdGeneratorService,
    private val widgetService: WidgetService,
    private val mqttPublisher: MqttPublisher,
    private val nodeBffGateway: NodeBffGateway
) {
    fun addInvoice(userBoardAuthenticationData: UserBoardAuthenticationData): Mono<Invoice> {
        return idGeneratorService.generateId(IdType.WIDGET_ID)
            .flatMap { invoiceId ->
                widgetService.addWidget(
                    foreignWidgetId = invoiceId,
                    accountId = userBoardAuthenticationData.accountId,
                    boardId = userBoardAuthenticationData.boardId,
                    widgetType = WidgetType.INVOICE,
                    name = "Invoice $invoiceId"
                )
            }
            .flatMap {
                invoiceRepository.save(Invoice.from(it.widgetId, it.boardId, it.accountId))
            }
            .auditOnSuccess(mqttPublisher, AuditEvent.ADD_WIDGET)
            .auditOnError(mqttPublisher, AuditEvent.ADD_WIDGET)
            .logOnSuccess(message = "Successfully added widget")
            .logOnError(errorMessage = "Failed to add widget")
    }

    fun getInvoices(userAuthenticationData: UserAuthenticationData, widgetIds: List<WidgetId>): Flux<Invoice> {
        return invoiceRepository.findAllByWidgetIdInAndAccountId(widgetIds, userAuthenticationData.accountId)
    }

    fun getSeedData(
        widgetId: WidgetId,
        userBoardAuthenticationData: UserBoardAuthenticationData
    ): Mono<List<InvoiceSeedItem>> {
        return invoiceRepository.findByWidgetIdAndBoardId(widgetId, userBoardAuthenticationData.boardId)
            .map { it.seed }
    }

    fun addSeedData(
        widgetId: WidgetId,
        userBoardAuthenticationData: UserBoardAuthenticationData,
        seedItemRequest: SeedItemRequest
    ): Mono<InvoiceSeedItem> {
        return invoiceRepository.findByWidgetIdAndBoardId(widgetId, userBoardAuthenticationData.boardId)
            .flatMap {
                invoiceRepository.save(it.addSeedItem(seedItemRequest))
            }
            .auditOnSuccess(mqttPublisher, AuditEvent.ADD_INVOICE_WIDGET_SEED)
            .auditOnError(mqttPublisher, AuditEvent.ADD_INVOICE_WIDGET_SEED)
            .logOnSuccess(message = "Successfully added invoice seed item")
            .logOnError(errorMessage = "Failed to add invoice seed item")
            .map { it.getSeedItem(seedItemRequest.code) }
    }

    fun updateSeedData(
        widgetId: WidgetId,
        seedCode: String,
        seedItemRequest: SeedItemRequest,
        userBoardAuthenticationData: UserBoardAuthenticationData
    ): Mono<InvoiceSeedItem> {
        return invoiceRepository.findByWidgetIdAndBoardId(widgetId, userBoardAuthenticationData.boardId)
            .flatMap {
                invoiceRepository.save(it.updateSeedItem(seedCode, seedItemRequest))
            }
            .auditOnSuccess(mqttPublisher, AuditEvent.UPDATE_INVOICE_WIDGET_SEED)
            .auditOnError(mqttPublisher, AuditEvent.UPDATE_INVOICE_WIDGET_SEED)
            .logOnSuccess(message = "Successfully updated invoice seed item")
            .logOnError(errorMessage = "Failed to update invoice seed item")
            .map { it.getSeedItem(seedItemRequest.code) }
    }

    fun resetItems(widgetId: WidgetId, boardData: BoardAuthenticationData): Mono<Invoice> {
        return invoiceRepository.findByWidgetIdAndBoardId(widgetId, boardData.boardId)
            .map {
                it.resetItems()
            }
            .flatMap {
                invoiceRepository.save(it)
            }
            .auditOnSuccess(mqttPublisher, AuditEvent.UPDATE_INVOICE_WIDGET_ITEM)
            .auditOnError(mqttPublisher, AuditEvent.UPDATE_INVOICE_WIDGET_ITEM)
            .logOnSuccess(message = "Successfully reset invoice cart")
            .logOnError(errorMessage = "Failed to reset invoice cart")
    }

    fun addItem(widgetId: WidgetId, code: String, boardData: BoardAuthenticationData): Mono<InvoiceWithError> {
        return invoiceRepository.findByWidgetIdAndBoardId(widgetId, boardData.boardId)
            .map { invoice ->
                invoice.addItem(code)
            }
            .flatMap {
                invoiceRepository.save(it)
            }
            .auditOnSuccess(mqttPublisher, AuditEvent.UPDATE_INVOICE_WIDGET_ITEM)
            .auditOnError(mqttPublisher, AuditEvent.UPDATE_INVOICE_WIDGET_ITEM)
            .logOnSuccess(message = "Successfully added item in invoice cart")
            .logOnError(errorMessage = "Failed to add item in invoice cart")
            .map { InvoiceWithError(it) }
            .onErrorResume {
                invoiceRepository.findByWidgetIdAndBoardId(widgetId, boardData.boardId)
                    .map { invoice ->
                        InvoiceWithError(invoice, it.message)
                    }
            }
    }

    fun removeItem(widgetId: WidgetId, code: String, boardData: BoardAuthenticationData): Mono<InvoiceWithError> {
        return invoiceRepository.findByWidgetIdAndBoardId(widgetId, boardData.boardId)
            .map {
                it.removeItem(code)
            }
            .flatMap { invoice ->
                invoiceRepository.save(invoice)
            }
            .auditOnSuccess(mqttPublisher, AuditEvent.UPDATE_INVOICE_WIDGET_ITEM)
            .auditOnError(mqttPublisher, AuditEvent.UPDATE_INVOICE_WIDGET_ITEM)
            .logOnSuccess(message = "Successfully removed item in invoice cart")
            .logOnError(errorMessage = "Failed to remove item in invoice cart")
            .map { InvoiceWithError(it) }
            .onErrorResume {
                invoiceRepository.findByWidgetIdAndBoardId(widgetId, boardData.boardId)
                    .map { invoice ->
                        InvoiceWithError(invoice, it.message)
                    }
            }
    }

    fun getInvoice(widgetId: WidgetId, boardData: BoardAuthenticationData): Mono<Invoice> {
        return invoiceRepository.findByWidgetIdAndBoardId(widgetId, boardData.boardId)
    }

    fun updatePaymentStatus(
        widgetId: WidgetId,
        paymentRequestBody: PaymentRequestBody,
        userBoardAuthenticationData: UserBoardAuthenticationData,
    ): Mono<Invoice> {
        return invoiceRepository.findByWidgetIdAndBoardId(widgetId, userBoardAuthenticationData.boardId)
            .flatMap {
                invoiceRepository.save(it.updatePaymentStatus(paymentRequestBody.paid))
            }
            .auditOnSuccess(mqttPublisher, AuditEvent.UPDATE_INVOICE_PAYMENT)
            .auditOnError(mqttPublisher, AuditEvent.UPDATE_INVOICE_PAYMENT)
            .logOnSuccess(message = "Successfully updated invoice payment")
            .logOnError(errorMessage = "Failed to update invoice payment")
            .flatMap { invoice ->
                val widget = Widget(
                    widgetId = invoice.widgetId,
                    widgetType = WidgetType.INVOICE,
                    accountId = invoice.accountId,
                    boardId = invoice.boardId
                )
                nodeBffGateway.updateInvoicePaymentStatus(widget, InvoiceState.from(invoice))
                    .map { invoice }
            }
    }
}
