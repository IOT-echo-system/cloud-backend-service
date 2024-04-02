package com.robotutor.iot.widgets.controllers.views

import com.robotutor.iot.widgets.modals.Widget
import com.robotutor.iot.widgets.modals.WidgetId
import com.robotutor.iot.widgets.modals.WidgetType
import java.time.LocalDateTime

data class WidgetView(
    val widgetId: WidgetId,
    val widgetType: WidgetType,
    val projectId: String,
    val boardId: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun from(widget: Widget): WidgetView {
            return WidgetView(
                widgetId = widget.foreignWidgetId,
                widgetType = widget.widgetType,
                projectId = widget.accountId,
                boardId = widget.boardId,
                createdAt = widget.createdAt
            )
        }
    }
}
