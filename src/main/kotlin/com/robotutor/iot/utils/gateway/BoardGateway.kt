package com.robotutor.iot.utils.gateway

import com.robotutor.iot.accounts.controllers.BoardController
import com.robotutor.iot.utils.models.UserAuthenticationData
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class BoardGateway(private val boardController: BoardController) {
    fun isValidBoard(
        userAuthenticationData: UserAuthenticationData,
        boardId: String
    ): Mono<Boolean> {
        return boardController.isValidBoardId(userAuthenticationData = userAuthenticationData, boardId)
    }
}
