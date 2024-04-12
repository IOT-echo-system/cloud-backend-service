package com.robotutor.iot.widgets.invoice.controllers

import com.robotutor.iot.utils.filters.annotations.RequirePolicy
import com.robotutor.iot.utils.models.BoardAuthenticationData
import com.robotutor.iot.utils.models.UserAuthenticationData
import com.robotutor.iot.utils.models.UserBoardAuthenticationData
import com.robotutor.iot.widgets.invoice.controllers.views.*
import com.robotutor.iot.widgets.invoice.services.InvoiceService
import com.robotutor.iot.widgets.modals.WidgetId
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/widgets/invoices")
class InvoiceController(private val invoiceService: InvoiceService) {

    @RequirePolicy("WIDGET_INVOICE_CREATE")
    @PostMapping
    fun addInvoice(userBoardAuthenticationData: UserBoardAuthenticationData): Mono<InvoiceView> {
        return invoiceService.addInvoice(userBoardAuthenticationData).map { InvoiceView.form(it) }
    }

    @RequirePolicy("WIDGET_INVOICE_GET")
    @GetMapping
    fun getInvoice(
        userAuthenticationData: UserAuthenticationData,
        @RequestParam widgetIds: List<WidgetId>
    ): Flux<InvoiceView> {
        return invoiceService.getInvoices(userAuthenticationData, widgetIds).map { InvoiceView.form(it) }
    }

    @RequirePolicy("WIDGET_INVOICE_UPDATE")
    @PutMapping("/{widgetId}/title")
    fun updateInvoiceTitle(
        @PathVariable widgetId: WidgetId,
        @Validated @RequestBody invoiceTitleRequest: InvoiceTitleRequest,
        userBoardAuthenticationData: UserBoardAuthenticationData
    ): Mono<InvoiceView> {
        return invoiceService.updateInvoiceTitle(widgetId, userBoardAuthenticationData, invoiceTitleRequest).map { InvoiceView.form(it) }
    }

    @RequirePolicy("WIDGET_INVOICE_SEED_UPDATE")
    @GetMapping("/{widgetId}/seed")
    fun getSeedData(@PathVariable widgetId: WidgetId, userBoardAuthenticationData: UserBoardAuthenticationData): Mono<List<InvoiceSeedDataView>> {
        return invoiceService.getSeedData(widgetId, userBoardAuthenticationData)
            .map { invoiceSeedItems ->
                invoiceSeedItems.map { invoiceSeedItem ->
                    InvoiceSeedDataView.from(invoiceSeedItem)
                }
            }
    }

    @RequirePolicy("WIDGET_INVOICE_SEED_UPDATE")
    @PostMapping("/{widgetId}/seed")
    fun addSeedData(
        @PathVariable widgetId: WidgetId,
        @Validated @RequestBody seedItemRequest: SeedItemRequest,
        userBoardAuthenticationData: UserBoardAuthenticationData
    ): Mono<InvoiceSeedDataView> {
        return invoiceService.addSeedData(widgetId, userBoardAuthenticationData, seedItemRequest).map { InvoiceSeedDataView.from(it) }
    }

    @RequirePolicy("WIDGET_INVOICE_SEED_UPDATE")
    @PutMapping("/{widgetId}/seed/{seedCode}")
    fun updateSeedData(
        @PathVariable widgetId: WidgetId,
        @PathVariable seedCode: String,
        @Validated @RequestBody seedItemRequest: SeedItemRequest,
        userBoardAuthenticationData: UserBoardAuthenticationData
    ): Mono<InvoiceSeedDataView> {
        return invoiceService.updateSeedData(widgetId, seedCode, seedItemRequest, userBoardAuthenticationData)
            .map { InvoiceSeedDataView.from(it) }
    }

    @RequirePolicy("WIDGET_INVOICE_ITEM_UPDATE")
    @PutMapping("/{widgetId}/items/reset")
    fun resetItems(
        @PathVariable widgetId: WidgetId,
        boardAuthenticationData: BoardAuthenticationData
    ): Mono<InvoiceState> {
        return invoiceService.resetItems(widgetId, boardAuthenticationData).map { InvoiceState.from(it) }
    }

    @RequirePolicy("WIDGET_INVOICE_ITEM_UPDATE")
    @PostMapping("/{widgetId}/items/{code}")
    fun addItem(
        @PathVariable widgetId: WidgetId,
        @PathVariable code: String,
        boardAuthenticationData: BoardAuthenticationData
    ): Mono<InvoiceState> {
        return invoiceService.addItem(widgetId, code, boardAuthenticationData)
            .map { InvoiceState.from(it.invoice, it.error) }
    }

    @RequirePolicy("WIDGET_INVOICE_ITEM_UPDATE")
    @DeleteMapping("/{widgetId}/items/{code}")
    fun removeItem(
        @PathVariable widgetId: WidgetId,
        @PathVariable code: String,
        boardAuthenticationData: BoardAuthenticationData
    ): Mono<InvoiceState> {
        return invoiceService.removeItem(widgetId, code, boardAuthenticationData)
            .map { InvoiceState.from(it.invoice, it.error) }
    }

    @RequirePolicy("WIDGET_INVOICE_ITEM_UPDATE")
    @GetMapping("/{widgetId}/state")
    fun getInvoiceState(
        @PathVariable widgetId: WidgetId,
        boardAuthenticationData: BoardAuthenticationData
    ): Mono<InvoiceState> {
        return invoiceService.getInvoice(widgetId, boardAuthenticationData).map { InvoiceState.from(it) }
    }

    @RequirePolicy("WIDGET_INVOICE_PAYMENT_UPDATE")
    @PutMapping("/{widgetId}/payment")
    fun updatePaymentState(
        @PathVariable widgetId: WidgetId,
        @Validated @RequestBody paymentRequestBody: PaymentRequestBody,
        userBoardAuthenticationData: UserBoardAuthenticationData
    ): Mono<InvoiceState> {
        return invoiceService.updatePaymentStatus(widgetId, paymentRequestBody, userBoardAuthenticationData).map { InvoiceState.from(it) }
    }
}
