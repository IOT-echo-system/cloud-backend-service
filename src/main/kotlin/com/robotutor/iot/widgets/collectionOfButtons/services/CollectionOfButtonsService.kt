package com.robotutor.iot.widgets.collectionOfButtons.services

import com.robotutor.iot.logging.logOnError
import com.robotutor.iot.logging.logOnSuccess
import com.robotutor.iot.mqtt.models.AuditEvent
import com.robotutor.iot.mqtt.services.MqttPublisher
import com.robotutor.iot.utils.audit.auditOnError
import com.robotutor.iot.utils.audit.auditOnSuccess
import com.robotutor.iot.utils.models.UserAuthenticationData
import com.robotutor.iot.utils.models.UserBoardAuthenticationData
import com.robotutor.iot.utils.services.IdGeneratorService
import com.robotutor.iot.webClient.WebClientWrapper
import com.robotutor.iot.widgets.collectionOfButtons.controllers.views.AddButtonRequest
import com.robotutor.iot.widgets.collectionOfButtons.modals.CollectionOfButtons
import com.robotutor.iot.widgets.collectionOfButtons.repositories.CollectionOfButtonsRepository
import com.robotutor.iot.widgets.config.NodeBffGatewayConfig
import com.robotutor.iot.widgets.modals.IdType
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
    private val webClientWrapper: WebClientWrapper,
    private val nodeBffGatewayConfig: NodeBffGatewayConfig
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
        return collectionOfButtonsRepository.findAllByWidgetIdAndAccountId(
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

}
