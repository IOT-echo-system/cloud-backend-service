package com.robotutor.iot.routines.repositories

import com.robotutor.iot.routines.models.Routine
import com.robotutor.iot.routines.models.RoutineId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface RoutineRepository : ReactiveCrudRepository<Routine, RoutineId> {
    fun findByRoutineIdAndAccountId(routineId: RoutineId, accountId: String): Mono<Routine>
    fun findAllByAccountId(accountId: String): Flux<Routine>
}
