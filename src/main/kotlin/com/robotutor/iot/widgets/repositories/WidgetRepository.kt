package com.robotutor.iot.widgets.repositories

import com.robotutor.iot.widgets.modals.Widget
import com.robotutor.iot.widgets.modals.WidgetId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WidgetRepository:ReactiveCrudRepository<Widget, WidgetId> {

}
