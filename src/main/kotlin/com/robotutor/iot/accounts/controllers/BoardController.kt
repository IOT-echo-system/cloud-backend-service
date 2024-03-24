package com.robotutor.iot.accounts.controllers

import com.robotutor.iot.accounts.controllers.views.AddBoardRequest
import com.robotutor.iot.accounts.controllers.views.BoardView
import com.robotutor.iot.accounts.models.BoardId
import com.robotutor.iot.accounts.services.BoardService
import com.robotutor.iot.utils.filters.annotations.RequirePolicy
import com.robotutor.iot.utils.models.UserAuthenticationData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/boards")
class BoardController(private val boardService: BoardService) {

    @RequirePolicy("BOARD_GET")
    @GetMapping
    fun getBoards(userAuthenticationData: UserAuthenticationData): Flux<BoardView> {
        return boardService.getBoards(userAuthenticationData).map { board -> BoardView.from(board) }
    }

    @RequirePolicy("BOARD_CREATE")
    @PostMapping
    fun addNewBoard(
        @RequestBody @Validated addBoardRequest: AddBoardRequest,
        userAuthenticationData: UserAuthenticationData
    ): Mono<BoardView> {
        return boardService.addNewBoard(addBoardRequest, userAuthenticationData).map { board -> BoardView.from(board) }
    }

    @RequirePolicy("BOARD_UPDATE")
    @PutMapping("/{boardId}/name")
    fun updateBoardName(
        @RequestBody @Validated addBoardRequest: AddBoardRequest,
        @PathVariable boardId: BoardId,
        userAuthenticationData: UserAuthenticationData
    ): Mono<BoardView> {
        return boardService.updateBoardName(addBoardRequest, boardId, userAuthenticationData.accountId).map { board -> BoardView.from(board) }
    }
}
