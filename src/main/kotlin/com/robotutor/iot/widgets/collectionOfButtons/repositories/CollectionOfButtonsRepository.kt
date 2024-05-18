package com.robotutor.iot.widgets.collectionOfButtons.repositories

import com.robotutor.iot.widgets.collectionOfButtons.modals.CollectionOfButtons
import com.robotutor.iot.widgets.modals.WidgetId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface CollectionOfButtonsRepository : ReactiveCrudRepository<CollectionOfButtons, WidgetId> {
    fun findAllByWidgetIdInAndAccountId(widgetId: List<WidgetId>, accountId: String): Flux<CollectionOfButtons>
    fun findByWidgetIdAndAccountId(widgetId: WidgetId, accountId: String): Mono<CollectionOfButtons>
}
