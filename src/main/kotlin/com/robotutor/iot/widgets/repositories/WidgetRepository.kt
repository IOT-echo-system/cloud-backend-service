package com.robotutor.iot.widgets.repositories

import com.robotutor.iot.accounts.models.BoardId
import com.robotutor.iot.widgets.modals.Widget
import com.robotutor.iot.widgets.modals.WidgetId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface WidgetRepository : ReactiveCrudRepository<Widget, WidgetId> {
    fun findAllByBoardIdIn(boardId: List<BoardId>): Flux<Widget>
    fun findByWidgetIdAndBoardId(widgetId: WidgetId, boardId: BoardId): Mono<Widget>
}
