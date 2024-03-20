package com.robotutor.iot.accounts.controllers

import com.robotutor.iot.accounts.controllers.views.AddBoardRequest
import com.robotutor.iot.accounts.controllers.views.BoardView
import com.robotutor.iot.accounts.services.BoardService
import com.robotutor.iot.utils.filters.annotations.RequirePolicy
import com.robotutor.iot.utils.models.UserAuthenticationData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/boards")
class BoardController(
    private val boardService: BoardService,
) {

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
//
//    @GetMapping("/account-details")
//    fun getAccountDetails(userAuthenticationData: UserAuthenticationData): Mono<AccountView> {
//        return accountService.getAccountDetails(userAuthenticationData)
//            .flatMap { account ->
//                roleService.getRoles(account.users.first().roles)
//                    .flatMap { roles ->
//                        val role = roles.find { it.roleId == userAuthenticationData.roleId }!!
//                        policyService.getPolicies(role.policies)
//                            .map{ policies ->
//                                AccountView.from(account, roles, policies)
//                            }
//                    }
//            }
//    }
//
//    @PostMapping("/validate")
//    fun isValidAccount(
//        @Validated @RequestBody accountValidationRequest: AccountValidationRequest,
//        userAuthenticationData: UserAuthenticationData
//    ): Mono<Map<String, Boolean>> {
//        return accountService.isAccountExistsWithRole(accountValidationRequest, userAuthenticationData.userId)
//    }
}
