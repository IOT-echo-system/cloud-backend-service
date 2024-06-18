package com.robotutor.iot.widgets.levelMonitor.services

import com.robotutor.iot.logging.logOnError
import com.robotutor.iot.logging.logOnSuccess
import com.robotutor.iot.mqtt.models.AuditEvent
import com.robotutor.iot.mqtt.services.MqttPublisher
import com.robotutor.iot.utils.audit.auditOnError
import com.robotutor.iot.utils.audit.auditOnSuccess
import com.robotutor.iot.utils.models.UserAuthenticationData
import com.robotutor.iot.utils.models.UserBoardAuthenticationData
import com.robotutor.iot.utils.services.IdGeneratorService
import com.robotutor.iot.widgets.gateway.NodeBffGateway
import com.robotutor.iot.widgets.levelMonitor.controllers.views.LevelMonitorValuesRequest
import com.robotutor.iot.widgets.levelMonitor.modals.LevelMonitor
import com.robotutor.iot.widgets.levelMonitor.repositories.LevelMonitorRepository
import com.robotutor.iot.widgets.modals.IdType
import com.robotutor.iot.widgets.modals.WidgetId
import com.robotutor.iot.widgets.modals.WidgetType
import com.robotutor.iot.widgets.services.WidgetService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Service
class LevelMonitorService(
    private val levelMonitorRepository: LevelMonitorRepository,
    private val idGeneratorService: IdGeneratorService,
    private val widgetService: WidgetService,
    private val mqttPublisher: MqttPublisher,
    private val nodeBffGateway: NodeBffGateway
) {
    fun addLevelMonitor(userBoardAuthenticationData: UserBoardAuthenticationData): Mono<LevelMonitor> {
        return idGeneratorService.generateId(IdType.WIDGET_ID)
            .flatMap { widgetId ->
                widgetService.addWidget(
                    foreignWidgetId = widgetId,
                    accountId = userBoardAuthenticationData.accountId,
                    boardId = userBoardAuthenticationData.boardId,
                    widgetType = WidgetType.LEVEL_MONITOR,
                    name = "Level monitor $widgetId"
                )
            }
            .flatMap {
                levelMonitorRepository.save(LevelMonitor.from(it.widgetId, it.boardId, it.accountId))
            }
            .auditOnSuccess(mqttPublisher, AuditEvent.ADD_WIDGET)
            .auditOnError(mqttPublisher, AuditEvent.ADD_WIDGET)
            .logOnSuccess(message = "Successfully added widget")
            .logOnError(errorMessage = "Failed to add widget")
    }

    fun getAllLevelMonitor(
        userAuthenticationData: UserAuthenticationData,
        widgetIds: List<WidgetId>
    ): Flux<LevelMonitor> {
        return levelMonitorRepository.findAllByWidgetIdInAndAccountId(widgetIds, userAuthenticationData.accountId)
    }

    fun updateValues(
        widgetId: WidgetId,
        levelMonitorValuesRequest: LevelMonitorValuesRequest,
        userBoardAuthenticationData: UserBoardAuthenticationData
    ): Mono<LevelMonitor> {
        return levelMonitorRepository.findByWidgetIdAndBoardId(widgetId, userBoardAuthenticationData.boardId)
            .map { it.updateValues(levelMonitorValuesRequest) }
            .flatMap { levelMonitorRepository.save(it) }
            .auditOnSuccess(mqttPublisher, AuditEvent.UPDATE_LEVEL_MONITOR_WIDGET_VALUES)
            .auditOnError(mqttPublisher, AuditEvent.UPDATE_LEVEL_MONITOR_WIDGET_VALUES)
            .logOnSuccess(message = "Successfully updated values for level monitor")
            .logOnError(errorMessage = "Failed to update values for level monitor")
    }
}
