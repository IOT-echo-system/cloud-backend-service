package com.robotutor.iot.accounts.models

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

const val ROLE_COLLECTION = "roles"

@TypeAlias("Role")
@Document(ROLE_COLLECTION)
data class Role(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val roleId: RoleId,
    val name: String,
    val createdBy: String,
    val policies: List<String> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun from(roleId: String, name: String, createdBy: String, policies: List<PolicyId>): Role {
            return Role(roleId = roleId, name = name, createdBy = createdBy, policies = policies)
        }
    }
}

typealias RoleId = String
