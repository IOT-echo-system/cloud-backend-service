package com.robotutor.iot.accounts.models

import com.robotutor.iot.accounts.controllers.views.AddAccountRequest
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

const val ACCOUNT_COLLECTION = "accounts"

@TypeAlias("Account")
@Document(ACCOUNT_COLLECTION)
data class Account(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val accountId: AccountId,
    val name: String,
    val owner: String,
    val users: List<User> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now()
) {

    companion object {
        fun from(
            accountId: String,
            addAccountRequest: AddAccountRequest,
            userId: String
        ): Account {
            return Account(
                accountId = accountId,
                name = addAccountRequest.name,
                owner = userId,
                users = listOf(User(userId, getDefaultRoleIds()))
            )
        }

        private fun getDefaultRoleIds(): List<String> {
            return listOf(1, 2, 3).map { number -> number.toString().padStart(IdType.ROLE_ID.length, '0') }
        }
    }
}

data class User(val userId: String, val roleIds: List<RoleId>)

typealias AccountId = String
