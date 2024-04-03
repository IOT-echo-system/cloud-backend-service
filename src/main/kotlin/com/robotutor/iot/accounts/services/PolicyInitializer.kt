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
            Policy(policyId = getPolicyId("4"), name = "BOARD_DELETE"),
            Policy(policyId = getPolicyId("5"), name = "DEVICE_GET"),
            Policy(policyId = getPolicyId("6"), name = "DEVICE_CREATE"),
            Policy(policyId = getPolicyId("7"), name = "DEVICE_UPDATE"),
            Policy(policyId = getPolicyId("8"), name = "DEVICE_DELETE"),
            Policy(policyId = getPolicyId("9"), name = "WIDGET_GET"),
            Policy(policyId = getPolicyId("10"), name = "WIDGET_CREATE"),
            Policy(policyId = getPolicyId("11"), name = "WIDGET_UPDATE"),
            Policy(policyId = getPolicyId("12"), name = "WIDGET_DELETE"),
            Policy(policyId = getPolicyId("13"), name = "WIDGET_INVOICE_GET"),
            Policy(policyId = getPolicyId("14"), name = "WIDGET_INVOICE_CREATE"),
            Policy(policyId = getPolicyId("15"), name = "WIDGET_INVOICE_UPDATE"),
            Policy(policyId = getPolicyId("16"), name = "WIDGET_INVOICE_DELETE"),
            Policy(policyId = getPolicyId("17"), name = "WIDGET_INVOICE_SEED_UPDATE")
        )
        policies.forEach { policy ->
            policyRepository.findByPolicyId(policy.policyId)
                .switchIfEmpty { policyRepository.save(policy) }
                .block()
        }

    }

    fun getPolicyId(id: String) = id.padStart(IdType.POLICY_ID.length, '0')
}
