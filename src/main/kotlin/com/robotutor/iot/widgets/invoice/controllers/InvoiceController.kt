package com.robotutor.iot.widgets.invoice.controllers

import com.robotutor.iot.utils.filters.annotations.RequirePolicy
import com.robotutor.iot.utils.models.BoardAuthenticationData
import com.robotutor.iot.utils.models.BoardData
import com.robotutor.iot.utils.models.UserAuthenticationData
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
    fun addInvoice(boardData: BoardData): Mono<InvoiceView> {
        return invoiceService.addInvoice(boardData).map { InvoiceView.form(it) }
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
        boardData: BoardData
    ): Mono<InvoiceView> {
        return invoiceService.updateInvoiceTitle(widgetId, boardData, invoiceTitleRequest).map { InvoiceView.form(it) }
    }

    @RequirePolicy("WIDGET_INVOICE_SEED_UPDATE")
    @GetMapping("/{widgetId}/seed")
    fun getSeedData(@PathVariable widgetId: WidgetId, boardData: BoardData): Mono<List<InvoiceSeedDataView>> {
        return invoiceService.getSeedData(widgetId, boardData)
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
        boardData: BoardData
    ): Mono<InvoiceSeedDataView> {
        return invoiceService.addSeedData(widgetId, boardData, seedItemRequest).map { InvoiceSeedDataView.from(it) }
    }

    @RequirePolicy("WIDGET_INVOICE_SEED_UPDATE")
    @PutMapping("/{widgetId}/seed/{seedCode}")
    fun updateSeedData(
        @PathVariable widgetId: WidgetId,
        @PathVariable seedCode: String,
        @Validated @RequestBody seedItemRequest: SeedItemRequest,
        boardData: BoardData
    ): Mono<InvoiceSeedDataView> {
        return invoiceService.updateSeedData(widgetId, seedCode, seedItemRequest, boardData)
            .map { InvoiceSeedDataView.from(it) }
    }

    @RequirePolicy("WIDGET_INVOICE_ITEM_UPDATE")
    @PutMapping("/{widgetId}/items/reset")
    fun resetItems(@PathVariable widgetId: WidgetId, boardAuthenticationData: BoardAuthenticationData): Mono<InvoiceState> {
        return invoiceService.resetItems(widgetId, boardAuthenticationData).map { InvoiceState.from(it) }
    }

    @RequirePolicy("WIDGET_INVOICE_ITEM_UPDATE")
    @PostMapping("/{widgetId}/items")
    fun addItem(
        @PathVariable widgetId: WidgetId,
        @RequestBody @Validated itemsRequest: ItemRequest,
        boardAuthenticationData: BoardAuthenticationData
    ): Mono<InvoiceState> {
        return invoiceService.addItem(widgetId, itemsRequest, boardAuthenticationData).map { InvoiceState.from(it.invoice, it.error) }
    }

    @RequirePolicy("WIDGET_INVOICE_ITEM_UPDATE")
    @DeleteMapping("/{widgetId}/items")
    fun removeItem(
        @PathVariable widgetId: WidgetId,
        @RequestBody @Validated itemsRequest: ItemRequest,
        boardAuthenticationData: BoardAuthenticationData
    ): Mono<InvoiceState> {
        return invoiceService.removeItem(widgetId, itemsRequest, boardAuthenticationData)
            .map { InvoiceState.from(it.invoice, it.error) }
    }

    @RequirePolicy("WIDGET_INVOICE_ITEM_UPDATE")
    @GetMapping("/{widgetId}/state")
    fun getInvoiceState(@PathVariable widgetId: WidgetId, boardAuthenticationData: BoardAuthenticationData): Mono<InvoiceState> {
        return invoiceService.getInvoice(widgetId, boardAuthenticationData).map { InvoiceState.from(it) }
    }
}
