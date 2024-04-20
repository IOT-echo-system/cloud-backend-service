package com.robotutor.iot.widgets.services

import com.robotutor.iot.logging.logOnError
import com.robotutor.iot.logging.logOnSuccess
import com.robotutor.iot.mqtt.models.AuditEvent
import com.robotutor.iot.mqtt.services.MqttPublisher
import com.robotutor.iot.utils.audit.auditOnError
import com.robotutor.iot.utils.audit.auditOnSuccess
import com.robotutor.iot.utils.models.UserBoardAuthenticationData
import com.robotutor.iot.utils.services.IdGeneratorService
import com.robotutor.iot.widgets.controllers.views.WidgetTitleRequest
import com.robotutor.iot.widgets.modals.IdType
import com.robotutor.iot.widgets.modals.Widget
import com.robotutor.iot.widgets.modals.WidgetId
import com.robotutor.iot.widgets.modals.WidgetType
import com.robotutor.iot.widgets.repositories.WidgetRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class WidgetService(
    private val widgetRepository: WidgetRepository,
    private val idGeneratorService: IdGeneratorService,
    private val mqttPublisher: MqttPublisher,
) {
    fun addWidget(
        foreignWidgetId: WidgetId,
        accountId: String,
        boardId: String,
        widgetType: WidgetType
    ): Mono<Widget> {
        return idGeneratorService.generateId(IdType.WIDGET_ID)
            .flatMap { widgetId ->
                widgetRepository.save(
                    Widget.from(
                        widgetId = widgetId,
                        foreignWidgetId = foreignWidgetId,
                        accountId = accountId,
                        boardId = boardId,
                        widgetType = widgetType
                    )
                )
            }
    }

    fun getWidgets(boardIds: List<String>): Flux<Widget> {
        return widgetRepository.findAllByBoardIdIn(boardIds)
    }

    fun updateTitle(
        foreignWidgetId: WidgetId,
        widgetTitleRequest: WidgetTitleRequest,
        userBoardAuthenticationData: UserBoardAuthenticationData
    ): Mono<Widget> {
        return widgetRepository.findByForeignWidgetIdAndBoardId(foreignWidgetId, userBoardAuthenticationData.boardId)
            .map { it.updateTitle(widgetTitleRequest.name) }
            .flatMap { widgetRepository.save(it) }
            .auditOnSuccess(mqttPublisher, AuditEvent.UPDATE_WIDGET_TITLE)
            .auditOnError(mqttPublisher, AuditEvent.UPDATE_WIDGET_TITLE)
            .logOnSuccess(message = "Successfully updated widget title")
            .logOnError(errorMessage = "Failed to update widget title")
    }

}
