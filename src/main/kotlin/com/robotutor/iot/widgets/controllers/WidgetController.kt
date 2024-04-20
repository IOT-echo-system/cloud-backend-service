package com.robotutor.iot.widgets.controllers

import com.robotutor.iot.utils.filters.annotations.RequirePolicy
import com.robotutor.iot.utils.models.UserBoardAuthenticationData
import com.robotutor.iot.widgets.controllers.views.WidgetTitleRequest
import com.robotutor.iot.widgets.controllers.views.WidgetView
import com.robotutor.iot.widgets.services.WidgetService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/widgets")
class WidgetController(private val widgetService: WidgetService) {

    @RequirePolicy("WIDGET_GET")
    @GetMapping
    fun getWidgets(@RequestParam boardIds: List<String>): Flux<WidgetView> {
        return widgetService.getWidgets(boardIds).map { WidgetView.from(it) }
    }

    @RequirePolicy("WIDGET_UPDATE")
    @PutMapping("/{widgetId}/title")
    fun updateTitle(
        @PathVariable widgetId: String,
        @Validated @RequestBody widgetTitleRequest: WidgetTitleRequest,
        userBoardAuthenticationData: UserBoardAuthenticationData
    ): Mono<WidgetView> {
        println("update title request")
        return widgetService.updateTitle(widgetId, widgetTitleRequest, userBoardAuthenticationData)
            .map { WidgetView.from(it) }
    }

}
