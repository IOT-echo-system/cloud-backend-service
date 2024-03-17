package com.robotutor.iot.accounts.services

import com.robotutor.iot.accounts.models.IdType
import com.robotutor.iot.accounts.models.PolicyId
import com.robotutor.iot.accounts.models.Role
import com.robotutor.iot.accounts.models.RoleId
import com.robotutor.iot.accounts.repositories.RoleRepository
import com.robotutor.iot.utils.services.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RoleService(private val roleRepository: RoleRepository, private val idGeneratorService: IdGeneratorService) {
    fun getRoles(roles: List<RoleId>): Mono<List<Role>> {
        return roleRepository.findAllByRoleIdIn(roles).collectList()
    }

    fun getRoleByRoleId(roleId: RoleId): Mono<Role> {
        return roleRepository.findByRoleId(roleId)
    }

    fun addRole(name: String, createdBy: String, policies: List<PolicyId>): Mono<Role> {
        return idGeneratorService.generateId(IdType.ROLE_ID)
            .flatMap { roleId ->
                roleRepository.save(Role.from(roleId, name, createdBy, policies))
            }
    }

}
