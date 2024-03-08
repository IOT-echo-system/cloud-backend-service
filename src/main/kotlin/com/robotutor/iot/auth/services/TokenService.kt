package com.robotutor.iot.auth.services


import com.robotutor.iot.auth.controllers.view.ResetPasswordRequest
import com.robotutor.iot.auth.controllers.view.UserLoginRequest
import com.robotutor.iot.auth.controllers.view.ValidateTokenResponse
import com.robotutor.iot.auth.exceptions.IOTError
import com.robotutor.iot.auth.models.*
import com.robotutor.iot.auth.repositories.TokenRepository
import com.robotutor.iot.logging.logOnError
import com.robotutor.iot.logging.logOnSuccess
import com.robotutor.iot.mqtt.models.AuditEvent
import com.robotutor.iot.mqtt.services.MqttPublisher
import com.robotutor.iot.utils.audit.auditOnError
import com.robotutor.iot.utils.audit.auditOnSuccess
import com.robotutor.iot.utils.exceptions.UnAuthorizedException
import com.robotutor.iot.utils.services.IdGeneratorService
import com.robotutor.iot.utils.utils.createMonoError
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDateTime

@Service
class TokenService(
    private val tokenRepository: TokenRepository,
    private val idGeneratorService: IdGeneratorService,
    private val userService: UserService,
    private val mqttPublisher: MqttPublisher
) {

    fun login(userLoginRequest: UserLoginRequest): Mono<Token> {
        return userService.verifyCredentials(userLoginRequest)
            .flatMap { generateToken(it.userId, LocalDateTime.now().plusDays(7), null) }
    }


    fun validate(token: String): Mono<ValidateTokenResponse> {
        return tokenRepository.findByValueAndExpiredAtAfter(token)
            .map { ValidateTokenResponse(userId = it.userId, accountId = it.accountId ?: "", roleId = it.roleId ?: "") }
            .switchIfEmpty {
                createMonoError(UnAuthorizedException(IOTError.IOT0103))
            }
            .logOnSuccess(message = "Successfully validated token")
            .logOnError(errorCode = IOTError.IOT0103.errorCode, errorMessage = "Failed to validate token")
    }

    fun generateToken(userId: UserId, expiredAt: LocalDateTime, otpId: OtpId?): Mono<Token> {
        return idGeneratorService.generateId(IdType.TOKEN_ID)
            .flatMap { tokenId ->
                tokenRepository.save(
                    Token.generate(
                        tokenId = tokenId,
                        userId = userId,
                        expiredAt = expiredAt,
                        otpId = otpId
                    )
                )
                    .auditOnSuccess(
                        mqttPublisher = mqttPublisher,
                        event = AuditEvent.GENERATE_TOKEN,
                        metadata = mapOf("tokenId" to tokenId),
                        userId = userId
                    )
            }
            .auditOnError(mqttPublisher = mqttPublisher, event = AuditEvent.GENERATE_TOKEN, userId = userId)
            .logOnSuccess(message = "Successfully generated token")
            .logOnError(errorMessage = "Failed to generate token")
    }

    fun resetPassword(resetPasswordRequest: ResetPasswordRequest, tokenValue: String): Mono<UserDetails> {
        return tokenRepository.findByValueAndExpiredAtAfter(tokenValue)
            .flatMap { token ->
                resetPassword(token, resetPasswordRequest)
                    .logOnSuccess(message = "Successfully reset user password")
                    .logOnError(errorMessage = "Failed to reset user password")
                    .flatMap { userDetails ->
                        tokenRepository.save(token.setExpired())
                            .map { userDetails }
                    }
                    .logOnSuccess(message = "set current token as expired")
                    .logOnError(errorMessage = "Failed to set current token as expired")
            }
    }

    private fun resetPassword(token: Token, resetPasswordRequest: ResetPasswordRequest): Mono<UserDetails> {
        return if (token.otpId != null) {
            userService.resetPassword(token.userId, resetPasswordRequest.password)
        } else {
            userService.resetPassword(
                token.userId,
                resetPasswordRequest.currentPassword ?: "",
                resetPasswordRequest.password
            )
        }
    }
}
