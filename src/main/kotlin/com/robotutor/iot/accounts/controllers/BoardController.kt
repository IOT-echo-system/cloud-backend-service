package com.robotutor.iot.accounts.controllers

import com.robotutor.iot.accounts.controllers.views.AddBoardRequest
import com.robotutor.iot.accounts.controllers.views.BoardView
import com.robotutor.iot.accounts.services.BoardService
import com.robotutor.iot.utils.filters.annotations.RequirePolicy
import com.robotutor.iot.utils.models.UserAuthenticationData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/boards")
class BoardController(
    private val boardService: BoardService,
) {

//    @GetMapping
//    fun getAccounts(userAuthenticationData: UserAuthenticationData): Flux<AccountWithRoles> {
//        return accountService.getAccounts(userAuthenticationData.userId)
//            .flatMap { account ->
//                roleService.getRoles(account.users.first().roles)
//                    .map { roles -> AccountWithRoles.from(account, roles) }
//            }
//    }

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
