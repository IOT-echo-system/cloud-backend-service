package com.robotutor.iot.widgets.levelMonitor.modals

import com.robotutor.iot.widgets.levelMonitor.controllers.views.LevelMonitorValuesRequest
import com.robotutor.iot.widgets.modals.WidgetId
import com.robotutor.iot.widgets.modals.WidgetType
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime


const val LEVEL_MONITOR_COLLECTION = "levelMonitor"

@TypeAlias("LevelMonitor")
@Document(LEVEL_MONITOR_COLLECTION)

data class LevelMonitor(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val widgetId: WidgetId,
    val boardId: String,
    val accountId: String,
    val minValue: Double = 0.0,
    val maxValue: Double = 1.0,
    val value: Double = 0.0,
    var minRange: Double = 0.0,
    var maxRange: Double = 100.0,
    var symbol: String = "%",
    val widgetType: WidgetType = WidgetType.LEVEL_MONITOR,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    val lastModifiedDate: LocalDateTime = LocalDateTime.now(),
) {
    fun updateValues(levelMonitorValuesRequest: LevelMonitorValuesRequest): LevelMonitor {
        this.minRange = levelMonitorValuesRequest.minValue
        this.maxRange = levelMonitorValuesRequest.maxValue
        this.symbol = levelMonitorValuesRequest.symbol
        return this
    }

    companion object {
        fun from(widgetId: String, boardId: String, accountId: String): LevelMonitor {
            return LevelMonitor(widgetId = widgetId, boardId = boardId, accountId = accountId)
        }
    }
}
