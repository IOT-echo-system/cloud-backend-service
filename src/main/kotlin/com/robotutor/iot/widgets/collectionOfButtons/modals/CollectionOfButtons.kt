package com.robotutor.iot.widgets.collectionOfButtons.modals

import com.robotutor.iot.widgets.collectionOfButtons.controllers.views.AddButtonRequest
import com.robotutor.iot.widgets.modals.WidgetId
import com.robotutor.iot.widgets.modals.WidgetType
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime


const val COLLECTION_OF_BUTTONS_COLLECTION = "collectionOfButtons"

@TypeAlias("CollectionOfButtons")
@Document(COLLECTION_OF_BUTTONS_COLLECTION)


data class CollectionOfButtons(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val widgetId: WidgetId,
    val boardId: String,
    val accountId: String,
    val widgetType: WidgetType = WidgetType.COLLECTION_OF_BUTTONS,
    val buttons: MutableList<Button> = mutableListOf(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    val lastModifiedDate: LocalDateTime = LocalDateTime.now(),
) {
    fun addButton(buttonId: String, addButtonRequest: AddButtonRequest): CollectionOfButtons {
        val button = Button(
            buttonId = buttonId,
            name = addButtonRequest.name.trim(),
            type = addButtonRequest.type,
            mode = addButtonRequest.mode,
            min = addButtonRequest.min,
            max = addButtonRequest.max,
            value = 0
        )
        this.buttons.add(button)
        return this
    }

    companion object {
        fun from(widgetId: WidgetId, boardId: String, accountId: String): CollectionOfButtons {
            return CollectionOfButtons(
                widgetId = widgetId,
                boardId = boardId,
                accountId = accountId,
            )
        }
    }
}

data class Button(
    val buttonId: String,
    val name: String,
    val type: ButtonType,
    val mode: ButtonMode,
    val min: Int = 0,
    val max: Int = 1,
    val value: Int = 0,
)

enum class ButtonMode {
    INPUT,
    OUTPUT
}

enum class ButtonType {
    ANALOG,
    DIGITAL
}
