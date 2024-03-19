package com.robotutor.iot.accounts.controllers.views

import com.robotutor.iot.accounts.models.Board
import com.robotutor.iot.accounts.models.BoardId
import com.robotutor.iot.accounts.models.BoardStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class AddBoardRequest(
    @field:NotBlank(message = "Board name should not be blank")
    @field:Size(min = 4, max = 20, message = "Board name should not be less than 4 char or more than 20 char")
    val name: String
)

data class BoardView(
    val boardId: BoardId,
    val name: String,
    val status: BoardStatus,
    val createAt: LocalDateTime
) {
    companion object {
        fun from(board: Board): BoardView {
            return BoardView(
                boardId = board.boardId,
                name = board.name,
                status = board.status,
                createAt = board.createdAt
            )
        }
    }

}

//data class AccountValidationRequest(
//    @field:NotBlank(message = "AccountId should not be blank")
//    val accountId: AccountId,
//    @field:NotBlank(message = "RoleId should not be blank")
//    val roleId: RoleId
//)
