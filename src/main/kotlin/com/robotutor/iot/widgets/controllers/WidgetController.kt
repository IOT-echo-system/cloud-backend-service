package com.robotutor.iot.widgets.controllers

import com.robotutor.iot.widgets.services.WidgetService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/widgets")
class WidgetController(private val widgetService: WidgetService) {

}
