package com.robotutor.iot.accounts.repositories

import com.robotutor.iot.accounts.models.Account
import com.robotutor.iot.accounts.models.AccountId
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface AccountRepository : ReactiveCrudRepository<Account, AccountId> {
    @Query("{ 'users.userId': ?0 }")
    fun findAllByUserId(userId: String): Flux<Account>
    fun findByAccountId(accountId: AccountId): Mono<Account>
}
