package com.robotutor.iot.routines.controllers

import com.robotutor.iot.routines.controllers.views.AddRoutineRequest
import com.robotutor.iot.routines.controllers.views.RoutineView
import com.robotutor.iot.routines.models.RoutineId
import com.robotutor.iot.routines.services.RoutineService
import com.robotutor.iot.utils.filters.annotations.RequirePolicy
import com.robotutor.iot.utils.models.UserAuthenticationData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/routines")
class RoutineController(private val routineService: RoutineService) {
    @RequirePolicy("ROUTINE_GET")
    @GetMapping
    fun getRoutines(userAuthenticationData: UserAuthenticationData): Flux<RoutineView> {
        return routineService.getRoutines(userAuthenticationData.accountId)
            .map { routine -> RoutineView.from(routine) }
    }

    @RequirePolicy("ROUTINE_CREATE")
    @PostMapping
    fun addNewRoutines(
        @RequestBody @Validated addRoutineRequest: AddRoutineRequest,
        userAuthenticationData: UserAuthenticationData
    ): Mono<RoutineView> {
        return routineService.addNewRoutine(addRoutineRequest, userAuthenticationData)
            .map { routine -> RoutineView.from(routine) }
    }

    @RequirePolicy("PROJECT_UPDATE")
    @PutMapping("/{routineId}/name")
    fun updateProjectName(
        @RequestBody @Validated addRoutineRequest: AddRoutineRequest,
        @PathVariable routineId: RoutineId,
        userAuthenticationData: UserAuthenticationData
    ): Mono<RoutineView> {
        return routineService.updateRoutineName(addRoutineRequest.name, routineId, userAuthenticationData)
            .map { routine -> RoutineView.from(routine) }
    }
}
