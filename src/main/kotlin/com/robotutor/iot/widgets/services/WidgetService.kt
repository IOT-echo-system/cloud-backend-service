package com.robotutor.iot.widgets.services

import com.robotutor.iot.utils.services.IdGeneratorService
import com.robotutor.iot.widgets.modals.IdType
import com.robotutor.iot.widgets.modals.Widget
import com.robotutor.iot.widgets.modals.WidgetId
import com.robotutor.iot.widgets.modals.WidgetType
import com.robotutor.iot.widgets.repositories.WidgetRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class WidgetService(
    private val widgetRepository: WidgetRepository,
    private val idGeneratorService: IdGeneratorService
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

}
