package com.robotutor.iot.widgets.levelMonitor.controllers

import com.robotutor.iot.utils.filters.annotations.RequirePolicy
import com.robotutor.iot.utils.models.BoardAuthenticationData
import com.robotutor.iot.utils.models.UserAuthenticationData
import com.robotutor.iot.utils.models.UserBoardAuthenticationData
import com.robotutor.iot.widgets.levelMonitor.controllers.views.CaptureValueRequest
import com.robotutor.iot.widgets.levelMonitor.controllers.views.LevelMonitorValuesRequest
import com.robotutor.iot.widgets.levelMonitor.controllers.views.LevelMonitorView
import com.robotutor.iot.widgets.levelMonitor.controllers.views.SensorValueRequest
import com.robotutor.iot.widgets.levelMonitor.services.LevelMonitorService
import com.robotutor.iot.widgets.modals.WidgetId
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/widgets/level-monitor")
class LevelMonitorController(private val levelMonitorService: LevelMonitorService) {

    @RequirePolicy("WIDGET_LEVEL_MONITOR_CREATE")
    @PostMapping
    fun addInvoice(userBoardAuthenticationData: UserBoardAuthenticationData): Mono<LevelMonitorView> {
        return levelMonitorService.addLevelMonitor(userBoardAuthenticationData).map { LevelMonitorView.form(it) }
    }

    @RequirePolicy("WIDGET_LEVEL_MONITOR_GET")
    @GetMapping
    fun getInvoice(
        userAuthenticationData: UserAuthenticationData,
        @RequestParam widgetIds: List<WidgetId>
    ): Flux<LevelMonitorView> {
        return levelMonitorService.getAllLevelMonitor(userAuthenticationData, widgetIds)
            .map { LevelMonitorView.form(it) }
    }

    @RequirePolicy("WIDGET_LEVEL_MONITOR_UPDATE")
    @PutMapping("/{widgetId}/values")
    fun updateValues(
        @PathVariable widgetId: WidgetId,
        @Validated @RequestBody levelMonitorValuesRequest: LevelMonitorValuesRequest,
        userBoardAuthenticationData: UserBoardAuthenticationData
    ): Mono<LevelMonitorView> {
        return levelMonitorService.updateValues(widgetId, levelMonitorValuesRequest, userBoardAuthenticationData)
            .map { LevelMonitorView.form(it) }
    }

    @RequirePolicy("WIDGET_LEVEL_MONITOR_UPDATE")
    @PutMapping("/{widgetId}/capture-value")
    fun captureValue(
        @PathVariable widgetId: WidgetId,
        @Validated @RequestBody captureValueRequest: CaptureValueRequest,
        userBoardAuthenticationData: UserBoardAuthenticationData
    ): Mono<LevelMonitorView> {
        return levelMonitorService.captureValue(widgetId, captureValueRequest, userBoardAuthenticationData)
            .map { LevelMonitorView.form(it) }
    }

    @RequirePolicy("WIDGET_LEVEL_MONITOR_SENSOR_VALUE_UPDATE")
    @PutMapping("/{widgetId}/value")
    fun updateSensorValue(
        @PathVariable widgetId: WidgetId,
        @Validated @RequestBody sensorValueRequest: SensorValueRequest,
        boardAuthenticationData: BoardAuthenticationData
    ): Mono<LevelMonitorView> {
        return levelMonitorService.updateValue(widgetId, sensorValueRequest, boardAuthenticationData)
            .map { LevelMonitorView.form(it) }
    }

}
