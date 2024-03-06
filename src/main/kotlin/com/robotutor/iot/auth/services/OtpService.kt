package com.robotutor.iot.auth.services

import com.robotutor.iot.auth.controllers.view.GenerateOtpRequest
import com.robotutor.iot.auth.controllers.view.VerifyOtpRequest
import com.robotutor.iot.auth.exceptions.IOTError
import com.robotutor.iot.auth.models.*
import com.robotutor.iot.auth.repositories.OtpRepository
import com.robotutor.iot.logging.logOnError
import com.robotutor.iot.logging.logOnSuccess
import com.robotutor.iot.mqtt.models.AuditEvent
import com.robotutor.iot.mqtt.models.CommunicationMessage
import com.robotutor.iot.mqtt.models.CommunicationType
import com.robotutor.iot.mqtt.models.MqttTopicName
import com.robotutor.iot.mqtt.services.MqttPublisher
import com.robotutor.iot.utils.audit.auditOnError
import com.robotutor.iot.utils.audit.auditOnSuccess
import com.robotutor.iot.utils.exceptions.BadDataException
import com.robotutor.iot.utils.exceptions.TooManyRequestsException
import com.robotutor.iot.utils.services.IdGeneratorService
import com.robotutor.iot.utils.utils.createMono
import com.robotutor.iot.utils.utils.createMonoError
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDateTime

@Service
class OtpService(
    private val idGeneratorService: IdGeneratorService,
    private val otpRepository: OtpRepository,
    private val tokenService: TokenService,
    private val userService: UserService,
    private val mqttPublisher: MqttPublisher
) {
    fun generateOtp(generateOtpRequest: GenerateOtpRequest): Mono<Otp> {
        return otpRepository.countByEmailAndCreatedAtAfter(
            generateOtpRequest.email,
            LocalDateTime.now().minusMinutes(10)
        )
            .flatMap { count ->
                if (count >= 3) {
                    createMonoError<Otp>(TooManyRequestsException(IOTError.IOT0104))
                        .logOnError(errorMessage = "Too many request for otp generation")
                } else {
                    otpRepository.findByEmailAndState(generateOtpRequest.email, OtpState.GENERATED)
                }
            }
            .flatMap {
                otpRepository.save(it.setExpired())
                    .logOnSuccess(message = "Set otp as expired")
                    .logOnError(errorMessage = "Failed to set otp as expired")
            }
            .switchIfEmpty {
                createMono(Otp(otpId = "vidisse", value = "nobis", email = "a@email.com", userId = "userId"))
            }
            .flatMap { userService.getUserByEmail(generateOtpRequest.email) }
            .flatMap { userDetails ->
                idGeneratorService.generateId(IdType.OTP_ID)
                    .flatMap { otpId ->
                        otpRepository.save(Otp.create(otpId, userDetails))
                            .map { sendEmail(it, userDetails) }
                            .auditOnSuccess(
                                mqttPublisher = mqttPublisher,
                                event = AuditEvent.GENERATE_OTP,
                                metadata = mapOf("otpId" to otpId),
                                userId = userDetails.userId
                            )
                    }
                    .auditOnError(
                        mqttPublisher = mqttPublisher,
                        event = AuditEvent.GENERATE_OTP,
                        userId = userDetails.userId
                    )
            }
            .logOnSuccess(message = "Successfully generated otp")
            .logOnError(errorMessage = "Failed to generate otp")
    }

    fun verifyOtp(verifyOtpRequest: VerifyOtpRequest): Mono<Token> {
        return otpRepository.findByOtpIdAndState(verifyOtpRequest.otpId, OtpState.GENERATED)
            .flatMap {
                if (it.isValidOtp(verifyOtpRequest.otp)) {
                    otpRepository.save(it.setVerified())
                        .auditOnSuccess(
                            mqttPublisher = mqttPublisher,
                            event = AuditEvent.VERIFY_OTP,
                            metadata = mapOf("otpId" to it.otpId),
                            userId = it.userId
                        )
                } else {
                    createMonoError<Otp>(BadDataException(IOTError.IOT0105))
                        .auditOnError(
                            mqttPublisher = mqttPublisher,
                            event = AuditEvent.VERIFY_OTP,
                            metadata = mapOf("otpId" to it.otpId),
                            userId = it.userId
                        )
                }
            }
            .logOnSuccess(message = "Successfully verified otp")
            .logOnError(errorMessage = "Failed to verify otp")
            .flatMap {
                tokenService.generateToken(
                    userId = it.userId,
                    expiredAt = LocalDateTime.now().plusMinutes(10),
                    otpId = it.otpId
                )
            }
    }

    private fun sendEmail(otp: Otp, userDetails: UserDetails): Otp {
        mqttPublisher.publish(
            MqttTopicName.COMMUNICATION, CommunicationMessage(
                type = CommunicationType.OTP,
                to = otp.email,
                userId = otp.userId,
                metadata = mapOf("name" to userDetails.name, "otp" to otp.value)
            )
        )
        return otp
    }
}
