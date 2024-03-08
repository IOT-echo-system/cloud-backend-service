package com.robotutor.iot.accounts.repositories

import com.robotutor.iot.accounts.models.Role
import com.robotutor.iot.accounts.models.RoleId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface RoleRepository : ReactiveCrudRepository<Role, RoleId> {
    fun findAllByRoleIdIn(roleId: List<RoleId>): Flux<Role>
    fun findByRoleId(roleId: RoleId): Mono<Role>
}
