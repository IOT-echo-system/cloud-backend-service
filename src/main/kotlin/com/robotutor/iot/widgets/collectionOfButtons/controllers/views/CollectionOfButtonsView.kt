package com.robotutor.iot.widgets.collectionOfButtons.controllers.views

import com.robotutor.iot.widgets.collectionOfButtons.modals.Button
import com.robotutor.iot.widgets.collectionOfButtons.modals.ButtonMode
import com.robotutor.iot.widgets.collectionOfButtons.modals.ButtonType
import com.robotutor.iot.widgets.collectionOfButtons.modals.CollectionOfButtons
import com.robotutor.iot.widgets.modals.WidgetId
import com.robotutor.iot.widgets.modals.WidgetType
import java.time.LocalDateTime

data class CollectionOfButtonsView(
    val widgetId: WidgetId,
    val boardId: String,
    val accountId: String,
    val widgetType: WidgetType,
    val buttons: List<ButtonView>,
    val createdAt: LocalDateTime,
    val lastModifiedDate: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun form(collectionOfButtons: CollectionOfButtons): CollectionOfButtonsView {
            return CollectionOfButtonsView(
                widgetId = collectionOfButtons.widgetId,
                boardId = collectionOfButtons.boardId,
                accountId = collectionOfButtons.accountId,
                widgetType = collectionOfButtons.widgetType,
                buttons = collectionOfButtons.buttons.map { ButtonView.from(it) },
                createdAt = collectionOfButtons.createdAt,
                lastModifiedDate = collectionOfButtons.lastModifiedDate
            )
        }
    }
}

data class ButtonView(
    val buttonId: String,
    val name: String,
    val type: ButtonType,
    val mode: ButtonMode,
    val min: Int,
    val max: Int,
    val value: Int,
) {
    companion object {
        fun from(button: Button): ButtonView {
            return ButtonView(
                buttonId = button.buttonId,
                name = button.name,
                type = button.type,
                mode = button.mode,
                min = button.min,
                max = button.max,
                value = button.value
            )
        }
    }
}
