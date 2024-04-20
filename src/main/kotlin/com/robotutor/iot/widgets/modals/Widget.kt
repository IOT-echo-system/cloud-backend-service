package com.robotutor.iot.widgets.modals

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime


const val WIDGET_COLLECTION = "widgets"

@TypeAlias("Widget")
@Document(WIDGET_COLLECTION)

data class Widget(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val widgetId: WidgetId,
    val foreignWidgetId: WidgetId,
    val widgetType: WidgetType,
    val accountId: String,
    val boardId: String,
    var title: String = "Widget",
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    fun updateTitle(name: String): Widget {
        this.title = name
        return this
    }

    companion object {
        fun from(
            widgetId: WidgetId,
            foreignWidgetId: WidgetId,
            accountId: String,
            boardId: String,
            widgetType: WidgetType
        ): Widget {
            return Widget(
                widgetId = widgetId,
                foreignWidgetId = foreignWidgetId,
                widgetType = widgetType,
                accountId = accountId,
                boardId = boardId
            )
        }
    }
}

enum class WidgetType {
    INVOICE
}

typealias WidgetId = String
