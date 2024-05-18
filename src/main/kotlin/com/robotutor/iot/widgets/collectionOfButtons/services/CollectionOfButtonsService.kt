package com.robotutor.iot.widgets.collectionOfButtons.services

import com.robotutor.iot.logging.logOnError
import com.robotutor.iot.logging.logOnSuccess
import com.robotutor.iot.mqtt.models.AuditEvent
import com.robotutor.iot.mqtt.services.MqttPublisher
import com.robotutor.iot.utils.audit.auditOnError
import com.robotutor.iot.utils.audit.auditOnSuccess
import com.robotutor.iot.utils.models.BoardAuthenticationData
import com.robotutor.iot.utils.models.UserAuthenticationData
import com.robotutor.iot.utils.models.UserBoardAuthenticationData
import com.robotutor.iot.utils.services.IdGeneratorService
import com.robotutor.iot.widgets.collectionOfButtons.controllers.views.AddButtonRequest
import com.robotutor.iot.widgets.collectionOfButtons.controllers.views.ButtonState
import com.robotutor.iot.widgets.collectionOfButtons.modals.CollectionOfButtons
import com.robotutor.iot.widgets.collectionOfButtons.repositories.CollectionOfButtonsRepository
import com.robotutor.iot.widgets.gateway.NodeBffGateway
import com.robotutor.iot.widgets.modals.IdType
import com.robotutor.iot.widgets.modals.Widget
import com.robotutor.iot.widgets.modals.WidgetId
import com.robotutor.iot.widgets.modals.WidgetType
import com.robotutor.iot.widgets.services.WidgetService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CollectionOfButtonsService(
    private val collectionOfButtonsRepository: CollectionOfButtonsRepository,
    private val idGeneratorService: IdGeneratorService,
    private val widgetService: WidgetService,
    private val mqttPublisher: MqttPublisher,
    private val nodeBffGateway: NodeBffGateway
) {

    fun addCollectionOfButtons(userBoardAuthenticationData: UserBoardAuthenticationData): Mono<CollectionOfButtons> {
        return idGeneratorService.generateId(IdType.WIDGET_ID)
            .flatMap { widgetId ->
                widgetService.addWidget(
                    foreignWidgetId = widgetId,
                    accountId = userBoardAuthenticationData.accountId,
                    boardId = userBoardAuthenticationData.boardId,
                    widgetType = WidgetType.COLLECTION_OF_BUTTONS,
                    name = "Collection of Buttons",
                )
            }
            .flatMap {
                collectionOfButtonsRepository.save(CollectionOfButtons.from(it.widgetId, it.boardId, it.accountId))
            }
            .auditOnSuccess(mqttPublisher, AuditEvent.ADD_WIDGET)
            .auditOnError(mqttPublisher, AuditEvent.ADD_WIDGET)
            .logOnSuccess(message = "Successfully added Collection of buttons widget")
            .logOnError(errorMessage = "Failed to add Collection of buttons widget")
    }

    fun getCollectionOfButtons(
        userAuthenticationData: UserAuthenticationData,
        widgetIds: List<WidgetId>
    ): Flux<CollectionOfButtons> {
        return collectionOfButtonsRepository.findAllByWidgetIdInAndAccountId(
            widgetIds,
            userAuthenticationData.accountId
        )
    }

    fun addButton(
        widgetId: WidgetId,
        addButtonRequest: AddButtonRequest,
        userBoardAuthenticationData: UserBoardAuthenticationData
    ): Mono<CollectionOfButtons> {
        return collectionOfButtonsRepository.findByWidgetIdAndAccountId(
            widgetId,
            userBoardAuthenticationData.accountId
        )
            .flatMap { collectionOfButtons ->
                idGeneratorService.generateId(IdType.BUTTON_ID)
                    .map { buttonId -> collectionOfButtons.addButton(buttonId, addButtonRequest) }
            }
            .flatMap { collectionOfButtonsRepository.save(it) }
            .auditOnSuccess(mqttPublisher, AuditEvent.COLLECTION_OF_BUTTONS_ADD_BUTTON)
            .auditOnError(mqttPublisher, AuditEvent.COLLECTION_OF_BUTTONS_ADD_BUTTON)
            .logOnSuccess(message = "Successfully added button in Collection of buttons widget")
            .logOnError(errorMessage = "Failed to add button in Collection of buttons widget")

    }

    fun updateButton(
        widgetId: WidgetId,
        buttonId: String,
        addButtonRequest: AddButtonRequest,
        userBoardAuthenticationData: UserBoardAuthenticationData
    ): Mono<CollectionOfButtons> {
        return collectionOfButtonsRepository.findByWidgetIdAndAccountId(
            widgetId,
            userBoardAuthenticationData.accountId
        )
            .map { it.updateButton(buttonId, addButtonRequest) }
            .flatMap { collectionOfButtonsRepository.save(it) }
            .auditOnSuccess(mqttPublisher, AuditEvent.COLLECTION_OF_BUTTONS_UPDATE_BUTTON)
            .auditOnError(mqttPublisher, AuditEvent.COLLECTION_OF_BUTTONS_UPDATE_BUTTON)
            .logOnSuccess(message = "Successfully updated button in Collection of buttons widget")
            .logOnError(errorMessage = "Failed to update button in Collection of buttons widget")
    }

    fun deleteButton(
        widgetId: WidgetId,
        buttonId: String,
        userBoardAuthenticationData: UserBoardAuthenticationData
    ): Mono<CollectionOfButtons> {
        return collectionOfButtonsRepository.findByWidgetIdAndAccountId(
            widgetId,
            userBoardAuthenticationData.accountId
        )
            .map { it.deleteButton(buttonId) }
            .flatMap { collectionOfButtonsRepository.save(it) }
            .auditOnSuccess(mqttPublisher, AuditEvent.COLLECTION_OF_BUTTONS_DELETE_BUTTON)
            .auditOnError(mqttPublisher, AuditEvent.COLLECTION_OF_BUTTONS_DELETE_BUTTON)
            .logOnSuccess(message = "Successfully deleted button in Collection of buttons widget")
            .logOnError(errorMessage = "Failed to delete button in Collection of buttons widget")
    }

    fun updateButtonValue(
        widgetId: WidgetId,
        buttonId: String,
        value: Int,
        userBoardAuthenticationData: UserBoardAuthenticationData
    ): Mono<CollectionOfButtons> {
        return collectionOfButtonsRepository.findByWidgetIdAndAccountId(
            widgetId,
            userBoardAuthenticationData.accountId
        )
            .map { it.updateButtonValue(buttonId, value) }
            .flatMap { collectionOfButtonsRepository.save(it) }
            .flatMap { collectionOfButtons ->
                val widget = Widget(
                    widgetId = collectionOfButtons.widgetId,
                    widgetType = WidgetType.COLLECTION_OF_BUTTONS,
                    accountId = collectionOfButtons.accountId,
                    boardId = collectionOfButtons.boardId
                )
                val buttonState = ButtonState.from(collectionOfButtons, buttonId)
                nodeBffGateway.updateCollectionOfButtonsButtonStatus(widget, buttonState)
                    .map { collectionOfButtons }
            }
            .auditOnSuccess(mqttPublisher, AuditEvent.COLLECTION_OF_BUTTONS_UPDATE_BUTTON_VALUE)
            .auditOnError(mqttPublisher, AuditEvent.COLLECTION_OF_BUTTONS_UPDATE_BUTTON_VALUE)
            .logOnSuccess(message = "Successfully updated button value in Collection of buttons widget")
            .logOnError(errorMessage = "Failed to update button value in Collection of buttons widget")
    }

    fun updateSensorValue(
        widgetId: WidgetId,
        buttonId: String,
        value: Int,
        boardAuthenticationData: BoardAuthenticationData
    ): Mono<CollectionOfButtons> {
        return collectionOfButtonsRepository.findByWidgetIdAndAccountId(
            widgetId,
            boardAuthenticationData.accountId
        )
            .map { it.updateButtonValue(buttonId, value) }
            .flatMap { collectionOfButtonsRepository.save(it) }
            .auditOnSuccess(mqttPublisher, AuditEvent.COLLECTION_OF_BUTTONS_UPDATE_SENSOR_VALUE)
            .auditOnError(mqttPublisher, AuditEvent.COLLECTION_OF_BUTTONS_UPDATE_SENSOR_VALUE)
            .logOnSuccess(message = "Successfully updated sensor value in Collection of buttons widget")
            .logOnError(errorMessage = "Failed to update sensor value in Collection of buttons widget")
    }

}
