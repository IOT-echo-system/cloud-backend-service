package com.robotutor.iot.widgets.controllers

import com.robotutor.iot.utils.filters.annotations.RequirePolicy
import com.robotutor.iot.widgets.controllers.views.WidgetView
import com.robotutor.iot.widgets.services.WidgetService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/widgets")
class WidgetController(private val widgetService: WidgetService) {

    @RequirePolicy("WIDGET_GET")
    @GetMapping
    fun getWidgets(@RequestParam boardIds: List<String>): Flux<WidgetView> {
        return widgetService.getWidgets(boardIds).map { WidgetView.from(it) }
    }

}
