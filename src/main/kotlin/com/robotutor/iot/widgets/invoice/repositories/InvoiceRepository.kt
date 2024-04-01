package com.robotutor.iot.widgets.invoice.repositories

import com.robotutor.iot.widgets.invoice.modals.Invoice
import com.robotutor.iot.widgets.modals.WidgetId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface InvoiceRepository : ReactiveCrudRepository<Invoice, WidgetId>
