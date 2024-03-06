package com.robotutor.iot.auth.repositories

import com.robotutor.iot.auth.models.UserDetails
import com.robotutor.iot.auth.models.UserId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository : ReactiveCrudRepository<UserDetails, UserId> {
    fun existsByEmail(email: String): Mono<Boolean>
    fun findByEmail(email: String): Mono<UserDetails>
    fun findByUserId(userId: UserId): Mono<UserDetails>
}
