package com.robotutor.iot.auth.repositories

import com.robotutor.iot.auth.models.Token
import com.robotutor.iot.auth.models.TokenId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Repository
interface TokenRepository : ReactiveCrudRepository<Token, TokenId> {
    fun findByValueAndExpiredAtAfter(token: String, expiredAtAfter: LocalDateTime = LocalDateTime.now()): Mono<Token>
    fun findByValue(token: String): Mono<Token>
    fun findByBoardIdAndExpiredAtAfter(
        boardId: String,
        expiredAtAfter: LocalDateTime = LocalDateTime.now()
    ): Mono<Token>

    fun deleteByBoardId(boardId: String): Mono<Token>
}
