package com.robotutor.iot.accounts.services

import com.robotutor.iot.accounts.models.IdType
import com.robotutor.iot.accounts.models.Role
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.switchIfEmpty

@Component
class RoleInitializer(private val roleService: RoleService) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        val roles = listOf(
            Role(
                roleId = "1".padStart(IdType.ROLE_ID.length, '0'),
                name = "Owner",
                createdBy = "System",
                policies = listOf()
            ),
            Role(
                roleId = "2".padStart(IdType.ROLE_ID.length, '0'),
                name = "Admin",
                createdBy = "System",
                policies = listOf()
            ),
            Role(
                roleId = "3".padStart(IdType.ROLE_ID.length, '0'),
                name = "User",
                createdBy = "System",
                policies = listOf()
            ),
        )
        roles.forEach { role ->
            roleService.getRoleByRoleId(role.roleId)
                .switchIfEmpty {
                    roleService.addRole(name = role.name, createdBy = role.createdBy)
                }
                .block()
        }

    }
}
