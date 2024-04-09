package com.robotutor.iot.accounts.models

import com.robotutor.iot.accounts.controllers.views.AddBoardRequest
import com.robotutor.iot.utils.models.UserAuthenticationData
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

const val BOARD_COLLECTION = "boards"

@TypeAlias("Board")
@Document(BOARD_COLLECTION)
data class Board(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val boardId: BoardId,
    val accountId: AccountId,
    var name: String,
    var status: BoardStatus = BoardStatus.UNHEALTHY,
    var statusUpdatedAt: LocalDateTime = LocalDateTime.now(),
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    fun updateName(name: String): Board {
        this.name = name
        return this
    }

    fun markHealthy(): Board {
        this.status = BoardStatus.HEALTHY
        this.statusUpdatedAt = LocalDateTime.now()
        return this
    }

    fun markUnHealthy(): Board {
        this.status = BoardStatus.UNHEALTHY
        this.statusUpdatedAt = LocalDateTime.now()
        return this
    }

    companion object {
        fun from(
            boardId: String,
            addBoardRequest: AddBoardRequest,
            userAuthenticationData: UserAuthenticationData
        ): Board {
            return Board(boardId = boardId, accountId = userAuthenticationData.accountId, name = addBoardRequest.name)
        }
    }
}

enum class BoardStatus {
    UNHEALTHY,
    HEALTHY
}

typealias BoardId = String
