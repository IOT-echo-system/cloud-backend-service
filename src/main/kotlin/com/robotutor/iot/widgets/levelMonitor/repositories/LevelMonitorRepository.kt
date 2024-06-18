package com.robotutor.iot.widgets.levelMonitor.repositories

import com.robotutor.iot.widgets.levelMonitor.modals.LevelMonitor
import com.robotutor.iot.widgets.modals.WidgetId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface LevelMonitorRepository : ReactiveCrudRepository<LevelMonitor, WidgetId> {
    fun findAllByWidgetIdInAndAccountId(widgetIds: List<WidgetId>, accountId: String): Flux<LevelMonitor>
    fun findByWidgetIdAndBoardId(widgetId: String, accountId: String): Mono<LevelMonitor>
}
