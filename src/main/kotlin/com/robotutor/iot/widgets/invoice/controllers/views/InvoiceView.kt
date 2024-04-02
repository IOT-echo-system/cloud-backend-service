package com.robotutor.iot.widgets.invoice.controllers.views

import com.robotutor.iot.widgets.invoice.modals.CartItem
import com.robotutor.iot.widgets.invoice.modals.Invoice
import com.robotutor.iot.widgets.modals.WidgetId
import com.robotutor.iot.widgets.modals.WidgetType
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class InvoiceView(
    val widgetId: WidgetId,
    val boardId: String,
    val projectId: String,
    val title: String,
    val widgetType: WidgetType,
    val cart: List<CartItem>,
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
                cart = invoice.cart,
                totalItems = invoice.totalItems,
                totalPrice = invoice.totalPrice,
                lastModifiedDate = invoice.lastModifiedDate,
            )
        }
    }
}


data class InvoiceTitleRequest(
    @field:Size(min = 4, max = 30, message = "Invoice title should not be less than 4 char or more than 30 char")
    val name: String
)
