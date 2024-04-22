package com.robotutor.iot.widgets.invoice.controllers.views

import com.robotutor.iot.widgets.invoice.modals.Invoice
import com.robotutor.iot.widgets.modals.WidgetState
import jakarta.validation.constraints.NotBlank

data class InvoiceState(val items: Int, val price: Double, val paid: Boolean, val error: String?) : WidgetState() {
    companion object {
        fun from(invoice: Invoice, error: String? = null): InvoiceState {
            return InvoiceState(
                items = invoice.cart.sumOf { it.unit },
                price = invoice.cart.sumOf { it.unit * it.pricePerUnit },
                paid = invoice.paid,
                error = error
            )
        }
    }
}


data class ItemRequest(
    @field:NotBlank(message = "Code should not be blank")
    val code: String
)
