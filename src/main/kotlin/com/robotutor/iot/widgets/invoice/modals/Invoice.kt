package com.robotutor.iot.widgets.invoice.modals

import com.robotutor.iot.utils.exceptions.DataNotFoundException
import com.robotutor.iot.utils.exceptions.DuplicateDataException
import com.robotutor.iot.widgets.exceptions.IOTError
import com.robotutor.iot.widgets.invoice.controllers.views.SeedItemRequest
import com.robotutor.iot.widgets.modals.WidgetId
import com.robotutor.iot.widgets.modals.WidgetType
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime


const val INVOICE_COLLECTION = "invoices"

@TypeAlias("Invoice")
@Document(INVOICE_COLLECTION)

data class Invoice(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val widgetId: WidgetId,
    val boardId: String,
    val accountId: String,
    var title: String = "Invoice",
    val widgetType: WidgetType = WidgetType.INVOICE,
    val seed: MutableList<InvoiceSeedItem> = mutableListOf(),
    val cart: MutableList<CartItem> = mutableListOf(),
    val totalItems: Int = 0,
    val totalPrice: Double = 0.0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    val lastModifiedDate: LocalDateTime = LocalDateTime.now(),
) {
    fun updateTitle(title: String): Invoice {
        this.title = title
        return this
    }

    fun addSeedItem(seedItemRequest: SeedItemRequest): Invoice {
        if (this.seed.any { it.code == seedItemRequest.code }) {
            throw DuplicateDataException(IOTError.IOT0501)
        }
        this.seed.add(InvoiceSeedItem.from(seedItemRequest))
        return this
    }

    fun getSeedItem(code: String): InvoiceSeedItem {
        val seedItem = this.seed.find { it.code == code }
        if (seedItem == null) {
            throw DataNotFoundException(IOTError.IOT0502)
        }
        return seedItem
    }

    companion object {
        fun from(widgetId: String, boardId: String, accountId: String): Invoice {
            return Invoice(widgetId = widgetId, boardId = boardId, accountId = accountId)
        }
    }
}

data class InvoiceSeedItem(val code: String, val name: String, val pricePerUnit: Double) {
    companion object {
        fun from(seedItemRequest: SeedItemRequest): InvoiceSeedItem {
            return InvoiceSeedItem(
                code = seedItemRequest.code,
                name = seedItemRequest.name,
                pricePerUnit = seedItemRequest.pricePerUnit
            )
        }
    }
}

data class CartItem(val code: String, val name: String, val pricePerUnit: Double, val unit: Int, val price: Double)
