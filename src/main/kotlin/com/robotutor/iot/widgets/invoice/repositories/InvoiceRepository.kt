package com.robotutor.iot.widgets.invoice.repositories

import com.robotutor.iot.widgets.invoice.modals.Invoice
import com.robotutor.iot.widgets.modals.WidgetId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface InvoiceRepository : ReactiveCrudRepository<Invoice, WidgetId> {
    fun findAllByWidgetIdInAndAccountId(widgetId: List<WidgetId>, accountId: String): Flux<Invoice>
    fun findByWidgetIdAndBoardId(widgetId: WidgetId, boardId: String): Mono<Invoice>
}
