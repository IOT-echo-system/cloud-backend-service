package com.robotutor.iot.accounts.scheduler

import com.robotutor.iot.accounts.models.BoardStatus
import com.robotutor.iot.accounts.repositories.BoardRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class BoardStatusScheduler(private val boardRepository: BoardRepository) {
    @Scheduled(cron = "0 0/3 * * * *")
    fun start() {
        boardRepository.findAllByStatusAndStatusUpdatedAtBefore(
            status = BoardStatus.HEALTHY,
            statusUpdatedAt = LocalDateTime.now().minusMinutes(3)
        )
            .flatMap {
                boardRepository.save(it.markUnHealthy())
            }
            .subscribe()
    }
}
