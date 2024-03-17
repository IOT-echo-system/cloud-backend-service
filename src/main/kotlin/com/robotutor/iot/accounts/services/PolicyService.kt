package com.robotutor.iot.accounts.services

import com.robotutor.iot.accounts.models.Policy
import com.robotutor.iot.accounts.models.PolicyId
import com.robotutor.iot.accounts.repositories.PolicyRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class PolicyService(private val policyRepository: PolicyRepository) {
    fun getPolicies(policies: List<PolicyId>): Mono<List<Policy>> {
        return policyRepository.findAllByPolicyIdIn(policies).collectList()
    }

}
