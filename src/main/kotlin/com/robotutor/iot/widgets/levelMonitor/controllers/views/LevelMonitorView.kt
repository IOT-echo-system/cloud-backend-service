package com.robotutor.iot.widgets.levelMonitor.controllers.views

import com.robotutor.iot.widgets.levelMonitor.modals.LevelMonitor
import com.robotutor.iot.widgets.modals.WidgetId
import com.robotutor.iot.widgets.modals.WidgetType
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class LevelMonitorView(
    val widgetId: WidgetId,
    val boardId: String,
    val projectId: String,
    val widgetType: WidgetType,
    val lastModifiedDate: LocalDateTime,
    val minValue: Double,
    val maxValue: Double,
    val value: Double,
    val actualValue: Double,
    val minRange: Double,
    val maxRange: Double,
    val symbol: String,
) {
    companion object {
        fun form(levelMonitor: LevelMonitor): LevelMonitorView {
            return LevelMonitorView(
                widgetId = levelMonitor.widgetId,
                boardId = levelMonitor.boardId,
                projectId = levelMonitor.accountId,
                widgetType = levelMonitor.widgetType,
                lastModifiedDate = levelMonitor.lastModifiedDate,
                minValue = levelMonitor.minValue,
                maxValue = levelMonitor.maxValue,
                value = levelMonitor.value,
                minRange = levelMonitor.minRange,
                maxRange = levelMonitor.maxRange,
                symbol = levelMonitor.symbol,
                actualValue = levelMonitor.actualValue
            )
        }
    }
}

data class LevelMonitorValuesRequest(
    @field:PositiveOrZero(message = "Min value must be positive or zero")
    val minValue: Double,

    @field:Positive(message = "Max value must be positive value")
    val maxValue: Double,

    @field:Size(min = 1, max = 12, message = "Symbol should not be less than 1 char or more than 12 char")
    val symbol: String
)

data class CaptureValueRequest(
    @field:Pattern(regexp = "^(min|max)$", message = "Type should be min or max")
    val type: String,
)

data class SensorValueRequest(
    val value: Double,
)
