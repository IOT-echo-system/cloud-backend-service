package com.robotutor.iot.widgets.controllers.views

import com.robotutor.iot.widgets.modals.Widget
import com.robotutor.iot.widgets.modals.WidgetId
import com.robotutor.iot.widgets.modals.WidgetType
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class WidgetTitleRequest(
    @field:Size(min = 4, max = 30, message = "Widget title should not be less than 4 char or more than 30 char")
    val name: String
)


data class WidgetView(
    val widgetId: WidgetId,
    val widgetType: WidgetType,
    val projectId: String,
    val boardId: String,
    val title: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun from(widget: Widget): WidgetView {
            return WidgetView(
                widgetId = widget.foreignWidgetId,
                widgetType = widget.widgetType,
                projectId = widget.accountId,
                boardId = widget.boardId,
                createdAt = widget.createdAt,
                title = widget.title
            )
        }
    }
}
