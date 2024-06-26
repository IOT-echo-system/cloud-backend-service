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
            Policy(policyId = getPolicyId("17"), name = "WIDGET_INVOICE_SEED_UPDATE"),
            Policy(policyId = getPolicyId("18"), name = "WIDGET_INVOICE_ITEM_UPDATE"),
            Policy(policyId = getPolicyId("19"), name = "BOARD_STATUS_UPDATE"),
            Policy(policyId = getPolicyId("20"), name = "WIDGET_INVOICE_PAYMENT_UPDATE"),
            Policy(policyId = getPolicyId("21"), name = "WIDGET_COLLECTION_OF_BUTTONS_GET"),
            Policy(policyId = getPolicyId("22"), name = "WIDGET_COLLECTION_OF_BUTTONS_CREATE"),
            Policy(policyId = getPolicyId("23"), name = "WIDGET_COLLECTION_OF_BUTTONS_UPDATE"),
            Policy(policyId = getPolicyId("24"), name = "WIDGET_COLLECTION_OF_BUTTONS_DELETE"),
            Policy(policyId = getPolicyId("25"), name = "WIDGET_COLLECTION_OF_BUTTONS_UPDATE_SENSOR_VALUE"),
            Policy(policyId = getPolicyId("26"), name = "PROJECT_UPDATE"),
            Policy(policyId = getPolicyId("27"), name = "ROUTINE_GET"),
            Policy(policyId = getPolicyId("28"), name = "ROUTINE_CREATE"),
            Policy(policyId = getPolicyId("29"), name = "ROUTINE_UPDATE"),
            Policy(policyId = getPolicyId("30"), name = "ROUTINE_DELETE"),
            Policy(policyId = getPolicyId("31"), name = "WIDGET_LEVEL_MONITOR_GET"),
            Policy(policyId = getPolicyId("32"), name = "WIDGET_LEVEL_MONITOR_CREATE"),
            Policy(policyId = getPolicyId("33"), name = "WIDGET_LEVEL_MONITOR_UPDATE"),
            Policy(policyId = getPolicyId("34"), name = "WIDGET_LEVEL_MONITOR_DELETE"),
            Policy(policyId = getPolicyId("35"), name = "WIDGET_LEVEL_MONITOR_SENSOR_VALUE_UPDATE"),
        )
        policies.forEach { policy ->
            policyRepository.findByPolicyId(policy.policyId)
                .switchIfEmpty { policyRepository.save(policy) }
                .block()
        }

    }

    fun getPolicyId(id: String) = id.padStart(IdType.POLICY_ID.length, '0')
}
