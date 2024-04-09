package com.robotutor.iot.auth.scheduler

import com.robotutor.iot.auth.models.OtpState
import com.robotutor.iot.auth.repositories.OtpRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class OtpStateUpdateScheduler(private val otpRepository: OtpRepository) {
    @Scheduled(cron = "0 * * * * *")
    fun start() {
        otpRepository.findAllByStateAndCreatedAtBefore(OtpState.GENERATED, LocalDateTime.now().minusMinutes(10))
            .flatMap {
                otpRepository.save(it.setExpired())
            }
            .subscribe()
    }
}
