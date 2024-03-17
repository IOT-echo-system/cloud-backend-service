package com.robotutor.iot.accounts.services

import com.robotutor.iot.accounts.models.IdType
import com.robotutor.iot.accounts.models.Policy
import com.robotutor.iot.accounts.repositories.PolicyRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.switchIfEmpty

@Component
class PolicyInitializer(private val policyRepository: PolicyRepository) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        val policies = listOf(
            Policy(policyId = getPolicyId("1"), name = "BOARD_GET"),
            Policy(policyId = getPolicyId("2"), name = "BOARD_CREATE"),
            Policy(policyId = getPolicyId("3"), name = "BOARD_UPDATE"),
            Policy(policyId = getPolicyId("4"), name = "BOARD_DELETE")
        )
        policies.forEach { policy ->
            policyRepository.findByPolicyId(policy.policyId)
                .switchIfEmpty { policyRepository.save(policy) }
                .block()
        }

    }

    fun getPolicyId(id: String) = id.padStart(IdType.POLICY_ID.length, '0')
}
