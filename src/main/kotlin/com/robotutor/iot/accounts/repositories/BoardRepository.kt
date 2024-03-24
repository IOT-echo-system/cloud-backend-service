package com.robotutor.iot.accounts.repositories

import com.robotutor.iot.accounts.models.AccountId
import com.robotutor.iot.accounts.models.Board
import com.robotutor.iot.accounts.models.BoardId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface BoardRepository : ReactiveCrudRepository<Board, BoardId> {
    fun findAllByAccountId(accountId: AccountId): Flux<Board>
    fun findByAccountIdAndBoardId(accountId: AccountId, boardId: BoardId): Mono<Board>
}
