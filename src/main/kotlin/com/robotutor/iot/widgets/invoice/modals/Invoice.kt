package com.robotutor.iot.widgets.invoice.modals

import com.robotutor.iot.utils.exceptions.BadDataException
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
    var paid: Boolean = false,
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

    fun updateSeedItem(seedCode: String, seedItemRequest: SeedItemRequest): Invoice {
        val index = this.seed.indexOfFirst { it.code == seedCode }
        if (index == -1) {
            throw DataNotFoundException(IOTError.IOT0502)
        }
        if (this.seed.any { it.code == seedItemRequest.code }) {
            throw DuplicateDataException(IOTError.IOT0501)
        }
        this.seed[index] = InvoiceSeedItem.from(seedItemRequest)
        return this
    }

    fun resetItems(): Invoice {
        this.cart.clear()
        this.paid = false
        return this
    }

    fun addItem(code: String): Invoice {
        if (this.paid) {
            throw BadDataException(IOTError.IOT0504)
        }
        val seedItem = this.seed.find { it.code == code }
        if (seedItem == null) {
            throw DataNotFoundException(IOTError.IOT0502)
        }
        val cartItem = this.cart.find { it.code == seedItem.code }
        if (cartItem == null) {
            this.cart.add(
                CartItem(
                    code = seedItem.code,
                    name = seedItem.name,
                    pricePerUnit = seedItem.pricePerUnit,
                    unit = 1,
                )
            )
        } else {
            cartItem.addUnit()
        }
        return this
    }

    fun removeItem(code: String): Invoice {
        if (this.paid) {
            throw BadDataException(IOTError.IOT0505)
        }
        val cartItem = this.getCartItem(code)
        if (cartItem == null) {
            throw DataNotFoundException(IOTError.IOT0503)
        } else if (cartItem.unit == 1) {
            this.cart.remove(cartItem)
        } else
            cartItem.removeUnit()
        return this
    }

    private fun getCartItem(code: String): CartItem? {
        val seedItem = this.seed.find { it.code == code }
        if (seedItem == null) {
            throw DataNotFoundException(IOTError.IOT0502)
        }
        return this.cart.find { it.code == seedItem.code }
    }

    fun updatePaymentStatus(paid: Boolean): Invoice {
        this.paid = paid
        return this
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

data class CartItem(val code: String, val name: String, val pricePerUnit: Double, var unit: Int) {
    fun addUnit(): CartItem {
        this.unit += 1
        return this
    }

    fun removeUnit(): CartItem {
        this.unit -= 1
        return this
    }
}

data class InvoiceWithError(val invoice: Invoice, val error: String? = null)
