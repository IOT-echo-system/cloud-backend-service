package com.robotutor.iot.widgets.invoice.controllers

import com.robotutor.iot.utils.filters.annotations.RequirePolicy
import com.robotutor.iot.utils.models.BoardData
import com.robotutor.iot.utils.models.UserAuthenticationData
import com.robotutor.iot.widgets.invoice.modals.Invoice
import com.robotutor.iot.widgets.invoice.services.InvoiceService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/widgets/invoices")
class InvoiceController(private val invoiceService: InvoiceService) {

    @RequirePolicy("WIDGET_INVOICE_CREATE")
    @PostMapping
    fun addInvoice(userAuthenticationData: UserAuthenticationData, boardData: BoardData): Mono<Invoice> {
        return invoiceService.addInvoice(boardData)
    }
}
