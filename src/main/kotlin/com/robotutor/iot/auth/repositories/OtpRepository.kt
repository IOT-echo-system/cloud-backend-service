package com.robotutor.iot.auth.repositories

import com.robotutor.iot.auth.models.Otp
import com.robotutor.iot.auth.models.OtpId
import com.robotutor.iot.auth.models.OtpState
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Repository
interface OtpRepository : ReactiveCrudRepository<Otp, OtpId> {
    fun countByEmailAndCreatedAtAfter(email: String, minusMinutes: LocalDateTime): Mono<Long>
    fun findByEmailAndState(email: String, otpState: OtpState): Mono<Otp>
    fun findByOtpIdAndState(otpId: String, otpState: OtpState): Mono<Otp>
    fun findAllByStateAndCreatedAtBefore(otpState: OtpState, minusMinutes: LocalDateTime): Flux<Otp>
}
