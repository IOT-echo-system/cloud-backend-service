package com.robotutor.iot.routines.controllers.views

import com.robotutor.iot.routines.models.Routine
import com.robotutor.iot.routines.models.RoutineId

data class RoutineView(
    val projectId: String,
    val routineId: RoutineId,
    val name: String,
    val description: String,
) {
    companion object {
        fun from(routine: Routine): RoutineView {
            return RoutineView(
                projectId = routine.accountId,
                routineId = routine.routineId,
                name = routine.name,
                description = routine.description
            )
        }
    }
}

