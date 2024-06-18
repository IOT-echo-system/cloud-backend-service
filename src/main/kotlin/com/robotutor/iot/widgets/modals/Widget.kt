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
    val widgetType: WidgetType,
    val accountId: String,
    val boardId: String,
    var title: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    fun updateTitle(name: String): Widget {
        this.title = name
        return this
    }

    companion object {
        fun from(
            widgetId: WidgetId,
            accountId: String,
            boardId: String,
            widgetType: WidgetType,
            name: String
        ): Widget {
            return Widget(
                widgetId = widgetId,
                widgetType = widgetType,
                accountId = accountId,
                boardId = boardId,
                title = name
            )
        }
    }
}

enum class WidgetType {
    INVOICE,
    COLLECTION_OF_BUTTONS,
    LEVEL_MONITOR
}

open class WidgetState

typealias WidgetId = String
