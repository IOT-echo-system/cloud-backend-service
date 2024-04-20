package com.robotutor.iot.widgets.collectionOfButtons.controllers

import com.robotutor.iot.utils.filters.annotations.RequirePolicy
import com.robotutor.iot.utils.models.UserAuthenticationData
import com.robotutor.iot.utils.models.UserBoardAuthenticationData
import com.robotutor.iot.widgets.collectionOfButtons.controllers.views.AddButtonRequest
import com.robotutor.iot.widgets.collectionOfButtons.controllers.views.CollectionOfButtonsView
import com.robotutor.iot.widgets.collectionOfButtons.services.CollectionOfButtonsService
import com.robotutor.iot.widgets.modals.WidgetId
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/widgets/collection-of-buttons")
class CollectionOfButtonsController(private val collectionOfButtonsService: CollectionOfButtonsService) {

    @RequirePolicy("WIDGET_COLLECTION_OF_BUTTONS_CREATE")
    @PostMapping
    fun addInvoice(userBoardAuthenticationData: UserBoardAuthenticationData): Mono<CollectionOfButtonsView> {
        return collectionOfButtonsService.addCollectionOfButtons(userBoardAuthenticationData)
            .map { CollectionOfButtonsView.form(it) }
    }

    @RequirePolicy("WIDGET_COLLECTION_OF_BUTTONS_GET")
    @GetMapping
    fun getInvoice(
        @RequestParam widgetIds: List<WidgetId>,
        userAuthenticationData: UserAuthenticationData
    ): Flux<CollectionOfButtonsView> {
        return collectionOfButtonsService.getCollectionOfButtons(userAuthenticationData, widgetIds)
            .map { CollectionOfButtonsView.form(it) }
    }


    @RequirePolicy("WIDGET_COLLECTION_OF_BUTTONS_UPDATE")
    @PostMapping("/{widgetId}/buttons")
    fun addButton(
        @PathVariable widgetId: WidgetId,
        @Validated @RequestBody addButtonRequest: AddButtonRequest,
        userBoardAuthenticationData: UserBoardAuthenticationData
    ): Mono<CollectionOfButtonsView> {
        return collectionOfButtonsService.addButton(widgetId, addButtonRequest, userBoardAuthenticationData)
            .map { CollectionOfButtonsView.form(it) }
    }

//    @RequirePolicy("WIDGET_INVOICE_SEED_UPDATE")
//    @PostMapping("/{widgetId}/seed")
//    fun addSeedData(
//        @PathVariable widgetId: WidgetId,
//        @Validated @RequestBody seedItemRequest: SeedItemRequest,
//        userBoardAuthenticationData: UserBoardAuthenticationData
//    ): Mono<InvoiceSeedDataView> {
//        return invoiceService.addSeedData(widgetId, userBoardAuthenticationData, seedItemRequest)
//            .map { InvoiceSeedDataView.from(it) }
//    }
//
//    @RequirePolicy("WIDGET_INVOICE_SEED_UPDATE")
//    @PutMapping("/{widgetId}/seed/{seedCode}")
//    fun updateSeedData(
//        @PathVariable widgetId: WidgetId,
//        @PathVariable seedCode: String,
//        @Validated @RequestBody seedItemRequest: SeedItemRequest,
//        userBoardAuthenticationData: UserBoardAuthenticationData
//    ): Mono<InvoiceSeedDataView> {
//        return invoiceService.updateSeedData(widgetId, seedCode, seedItemRequest, userBoardAuthenticationData)
//            .map { InvoiceSeedDataView.from(it) }
//    }
//
//    @RequirePolicy("WIDGET_INVOICE_ITEM_UPDATE")
//    @PutMapping("/{widgetId}/items/reset")
//    fun resetItems(
//        @PathVariable widgetId: WidgetId,
//        boardAuthenticationData: BoardAuthenticationData
//    ): Mono<InvoiceState> {
//        return invoiceService.resetItems(widgetId, boardAuthenticationData).map { InvoiceState.from(it) }
//    }
//
//    @RequirePolicy("WIDGET_INVOICE_ITEM_UPDATE")
//    @PostMapping("/{widgetId}/items/{code}")
//    fun addItem(
//        @PathVariable widgetId: WidgetId,
//        @PathVariable code: String,
//        boardAuthenticationData: BoardAuthenticationData
//    ): Mono<InvoiceState> {
//        return invoiceService.addItem(widgetId, code, boardAuthenticationData)
//            .map { InvoiceState.from(it.invoice, it.error) }
//    }
//
//    @RequirePolicy("WIDGET_INVOICE_ITEM_UPDATE")
//    @DeleteMapping("/{widgetId}/items/{code}")
//    fun removeItem(
//        @PathVariable widgetId: WidgetId,
//        @PathVariable code: String,
//        boardAuthenticationData: BoardAuthenticationData
//    ): Mono<InvoiceState> {
//        return invoiceService.removeItem(widgetId, code, boardAuthenticationData)
//            .map { InvoiceState.from(it.invoice, it.error) }
//    }
//
//    @RequirePolicy("WIDGET_INVOICE_ITEM_UPDATE")
//    @GetMapping("/{widgetId}/state")
//    fun getInvoiceState(
//        @PathVariable widgetId: WidgetId,
//        boardAuthenticationData: BoardAuthenticationData
//    ): Mono<InvoiceState> {
//        return invoiceService.getInvoice(widgetId, boardAuthenticationData).map { InvoiceState.from(it) }
//    }
//
//    @RequirePolicy("WIDGET_INVOICE_PAYMENT_UPDATE")
//    @PutMapping("/{widgetId}/payment")
//    fun updatePaymentState(
//        @PathVariable widgetId: WidgetId,
//        @Validated @RequestBody paymentRequestBody: PaymentRequestBody,
//        userBoardAuthenticationData: UserBoardAuthenticationData
//    ): Mono<InvoiceView> {
//        return invoiceService.updatePaymentStatus(widgetId, paymentRequestBody, userBoardAuthenticationData)
//            .map { InvoiceView.form(it) }
//    }
}
