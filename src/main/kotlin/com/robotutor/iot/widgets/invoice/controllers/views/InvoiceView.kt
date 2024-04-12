package com.robotutor.iot.widgets.invoice.controllers.views

import com.robotutor.iot.widgets.invoice.modals.CartItem
import com.robotutor.iot.widgets.invoice.modals.Invoice
import com.robotutor.iot.widgets.invoice.modals.InvoiceSeedItem
import com.robotutor.iot.widgets.modals.WidgetId
import com.robotutor.iot.widgets.modals.WidgetType
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class InvoiceView(
    val widgetId: WidgetId,
    val boardId: String,
    val projectId: String,
    val title: String,
    val widgetType: WidgetType,
    val cart: List<CartItemView>,
    val totalItems: Int,
    val totalPrice: Double,
    val lastModifiedDate: LocalDateTime
) {
    companion object {
        fun form(invoice: Invoice): InvoiceView {
            return InvoiceView(
                widgetId = invoice.widgetId,
                boardId = invoice.boardId,
                title = invoice.title,
                projectId = invoice.accountId,
                widgetType = invoice.widgetType,
                cart = invoice.cart.map { CartItemView.from(it) },
                totalItems = invoice.cart.sumOf { it.unit },
                totalPrice = invoice.cart.sumOf { it.unit * it.pricePerUnit },
                lastModifiedDate = invoice.lastModifiedDate,
            )
        }
    }
}

data class CartItemView(
    val code: String,
    val name: String,
    val pricePerUnit: Double,
    var unit: Int,
    val price: Double
) {
    companion object {
        fun from(cartItem: CartItem): CartItemView {
            return CartItemView(
                code = cartItem.code,
                name = cartItem.name,
                pricePerUnit = cartItem.pricePerUnit,
                unit = cartItem.unit,
                price = cartItem.pricePerUnit * cartItem.unit
            )
        }
    }
}

data class InvoiceSeedDataView(val code: String, val name: String, val pricePerUnit: Double) {
    companion object {
        fun from(invoiceSeedItem: InvoiceSeedItem): InvoiceSeedDataView {
            return InvoiceSeedDataView(
                code = invoiceSeedItem.code,
                name = invoiceSeedItem.name,
                pricePerUnit = invoiceSeedItem.pricePerUnit
            )
        }
    }
}


data class InvoiceTitleRequest(
    @field:Size(min = 4, max = 30, message = "Invoice title should not be less than 4 char or more than 30 char")
    val name: String
)

data class SeedItemRequest(
    @field:Size(min = 1, max = 12, message = "Seed item code should not be less than 1 char or more than 12 char")
    val code: String,
    @field:Size(min = 4, max = 30, message = "Seed item name should not be less than 4 char or more than 30 char")
    val name: String,
    @Min(value = 0, message = "Seed invoice price per Unit should a valid number")
    val pricePerUnit: Double
)


data class PaymentRequestBody(val paid: Boolean)
