package com.robotutor.iot.widgets.collectionOfButtons.controllers

import com.robotutor.iot.utils.filters.annotations.RequirePolicy
import com.robotutor.iot.utils.models.BoardAuthenticationData
import com.robotutor.iot.utils.models.UserAuthenticationData
import com.robotutor.iot.utils.models.UserBoardAuthenticationData
import com.robotutor.iot.widgets.collectionOfButtons.controllers.views.AddButtonRequest
import com.robotutor.iot.widgets.collectionOfButtons.controllers.views.ButtonValueRequest
import com.robotutor.iot.widgets.collectionOfButtons.controllers.views.CollectionOfButtonsView
import com.robotutor.iot.widgets.collectionOfButtons.services.CollectionOfButtonsService
import com.robotutor.iot.widgets.gateway.CloudBffGateway
import com.robotutor.iot.widgets.modals.WidgetId
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/widgets/collection-of-buttons")
class CollectionOfButtonsController(
    private val collectionOfButtonsService: CollectionOfButtonsService,
    private val cloudBffGateway: CloudBffGateway
) {

    @RequirePolicy("WIDGET_COLLECTION_OF_BUTTONS_CREATE")
    @PostMapping
    fun addInvoice(userBoardAuthenticationData: UserBoardAuthenticationData): Mono<CollectionOfButtonsView> {
        return collectionOfButtonsService.addCollectionOfButtons(userBoardAuthenticationData)
            .map { CollectionOfButtonsView.form(it) }
    }

    @RequirePolicy("WIDGET_COLLECTION_OF_BUTTONS_GET")
    @GetMapping
    fun getInvoice(
        @RequestParam widgetIds: List<WidgetId>,
        userAuthenticationData: UserAuthenticationData
    ): Flux<CollectionOfButtonsView> {
        return collectionOfButtonsService.getCollectionOfButtons(userAuthenticationData, widgetIds)
            .map { CollectionOfButtonsView.form(it) }
    }


    @RequirePolicy("WIDGET_COLLECTION_OF_BUTTONS_UPDATE")
    @PostMapping("/{widgetId}/buttons")
    fun addButton(
        @PathVariable widgetId: WidgetId,
        @Validated @RequestBody addButtonRequest: AddButtonRequest,
        userBoardAuthenticationData: UserBoardAuthenticationData
    ): Mono<CollectionOfButtonsView> {
        return collectionOfButtonsService.addButton(widgetId, addButtonRequest, userBoardAuthenticationData)
            .map { CollectionOfButtonsView.form(it) }
    }

    @RequirePolicy("WIDGET_COLLECTION_OF_BUTTONS_UPDATE")
    @PutMapping("/{widgetId}/buttons/{buttonId}")
    fun updateButton(
        @PathVariable widgetId: WidgetId,
        @PathVariable buttonId: String,
        @Validated @RequestBody addButtonRequest: AddButtonRequest,
        userBoardAuthenticationData: UserBoardAuthenticationData
    ): Mono<CollectionOfButtonsView> {
        return collectionOfButtonsService.updateButton(
            widgetId,
            buttonId,
            addButtonRequest,
            userBoardAuthenticationData
        ).map { CollectionOfButtonsView.form(it) }
    }

    @RequirePolicy("WIDGET_COLLECTION_OF_BUTTONS_UPDATE")
    @DeleteMapping("/{widgetId}/buttons/{buttonId}")
    fun deleteButton(
        @PathVariable widgetId: WidgetId,
        @PathVariable buttonId: String,
        userBoardAuthenticationData: UserBoardAuthenticationData
    ): Mono<CollectionOfButtonsView> {
        return collectionOfButtonsService.deleteButton(widgetId, buttonId, userBoardAuthenticationData)
            .map { CollectionOfButtonsView.form(it) }
    }

    @RequirePolicy("WIDGET_COLLECTION_OF_BUTTONS_UPDATE")
    @PutMapping("/{widgetId}/buttons/{buttonId}/value")
    fun updateButtonValue(
        @PathVariable widgetId: WidgetId,
        @PathVariable buttonId: String,
        @Validated @RequestBody buttonValueRequest: ButtonValueRequest,
        userBoardAuthenticationData: UserBoardAuthenticationData
    ): Mono<CollectionOfButtonsView> {
        return collectionOfButtonsService.updateButtonValue(
            widgetId,
            buttonId,
            buttonValueRequest.value,
            userBoardAuthenticationData
        )
            .map { CollectionOfButtonsView.form(it) }
    }

    @RequirePolicy("WIDGET_COLLECTION_OF_BUTTONS_UPDATE_SENSOR_VALUE")
    @PutMapping("/{widgetId}/sensors/{buttonId}/value")
    fun updateSensorValue(
        @PathVariable widgetId: WidgetId,
        @PathVariable buttonId: String,
        @Validated @RequestBody buttonValueRequest: ButtonValueRequest,
        boardAuthenticationData: BoardAuthenticationData
    ): Mono<CollectionOfButtonsView> {
        return collectionOfButtonsService.updateSensorValue(
            widgetId,
            buttonId,
            buttonValueRequest.value,
            boardAuthenticationData
        )
            .map { CollectionOfButtonsView.form(it) }
            .flatMap { collectionOfButtonsView ->
                cloudBffGateway.updateWidget(collectionOfButtonsView).map { collectionOfButtonsView }
            }
    }
}
