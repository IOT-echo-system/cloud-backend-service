package com.robotutor.iot.accounts.repositories

import com.robotutor.iot.accounts.models.Policy
import com.robotutor.iot.accounts.models.PolicyId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface PolicyRepository : ReactiveCrudRepository<Policy, PolicyId> {
    fun findByPolicyId(policyId: PolicyId): Mono<Policy>
    fun findAllByPolicyIdIn(policyIds: List<PolicyId>): Flux<Policy>
}
