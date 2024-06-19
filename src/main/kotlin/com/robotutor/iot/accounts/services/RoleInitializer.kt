package com.robotutor.iot.accounts.services

import com.robotutor.iot.accounts.models.IdType
import com.robotutor.iot.accounts.models.Role
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.switchIfEmpty

@Component
class RoleInitializer(private val roleService: RoleService, private val policyInitializer: PolicyInitializer) :
    ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        val roles = listOf(
            Role(roleId = "1".padStart(IdType.ROLE_ID.length, '0'),
                name = "Owner",
                createdBy = "System",
                policies = listOf(
                    "1",
                    "2",
                    "3",
                    "4",
                    "5",
                    "6",
                    "7",
                    "8",
                    "9",
                    "10",
                    "11",
                    "12",
                    "13",
                    "14",
                    "15",
                    "16",
                    "17",
                    "20",
                    "21",
                    "22",
                    "23",
                    "24",
                    "26",
                    "27",
                    "28",
                    "29",
                    "30",
                    "31",
                    "32",
                    "33",
                    "34"
                ).map { policyInitializer.getPolicyId(it) }),
            Role(roleId = "2".padStart(IdType.ROLE_ID.length, '0'),
                name = "Admin",
                createdBy = "System",
                policies = listOf(
                    "1",
                    "2",
                    "3",
                    "4",
                    "5",
                    "6",
                    "7",
                    "8",
                    "9",
                    "10",
                    "11",
                    "12",
                    "13",
                    "14",
                    "15",
                    "16",
                    "20",
                    "21",
                    "22",
                    "23",
                    "24"
                ).map { policyInitializer.getPolicyId(it) }),
            Role(roleId = "3".padStart(IdType.ROLE_ID.length, '0'),
                name = "User",
                createdBy = "System",
                policies = listOf(
                    "1", "5", "9", "13", "20", "21"
                ).map { policyInitializer.getPolicyId(it) }),
            Role(roleId = "4".padStart(IdType.ROLE_ID.length, '0'),
                name = "Board",
                createdBy = "System",
                policies = listOf(
                    "18", "19", "25", "35"
                ).map { policyInitializer.getPolicyId(it) }),
        )
        roles.forEach { role ->
            roleService.getRoleByRoleId(role.roleId).switchIfEmpty {
                roleService.addRole(name = role.name, createdBy = role.createdBy, policies = role.policies)
            }.block()
        }

    }
}
