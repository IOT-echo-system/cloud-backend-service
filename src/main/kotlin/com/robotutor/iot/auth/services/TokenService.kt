package com.robotutor.iot.auth.services


import com.robotutor.iot.auth.controllers.view.ResetPasswordRequest
import com.robotutor.iot.auth.controllers.view.UpdateTokenRequest
import com.robotutor.iot.auth.controllers.view.UserLoginRequest
import com.robotutor.iot.auth.controllers.view.ValidateTokenResponse
import com.robotutor.iot.auth.exceptions.IOTError
import com.robotutor.iot.auth.gateway.AccountServiceGateway
import com.robotutor.iot.auth.models.*
import com.robotutor.iot.auth.repositories.TokenRepository
import com.robotutor.iot.logging.logOnError
import com.robotutor.iot.logging.logOnSuccess
import com.robotutor.iot.mqtt.models.AuditEvent
import com.robotutor.iot.mqtt.services.MqttPublisher
import com.robotutor.iot.utils.audit.auditOnError
import com.robotutor.iot.utils.audit.auditOnSuccess
import com.robotutor.iot.utils.exceptions.UnAuthorizedException
import com.robotutor.iot.utils.models.UserAuthenticationData
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
    private val mqttPublisher: MqttPublisher,
    private val accountServiceGateway: AccountServiceGateway
) {

    fun login(userLoginRequest: UserLoginRequest): Mono<Token> {
        return userService.verifyCredentials(userLoginRequest)
            .flatMap { generateToken(userId = it.userId, expiredAt = LocalDateTime.now().plusDays(7)) }
    }


    fun validate(token: String): Mono<ValidateTokenResponse> {
        return tokenRepository.findByValueAndExpiredAtAfter(token)
            .map {
                ValidateTokenResponse(
                    userId = it.userId,
                    projectId = it.accountId ?: "",
                    roleId = it.roleId ?: "",
                    boardId = it.boardId
                )
            }
            .switchIfEmpty {
                createMonoError(UnAuthorizedException(IOTError.IOT0103))
            }
            .logOnSuccess(message = "Successfully validated token")
            .logOnError(errorCode = IOTError.IOT0103.errorCode, errorMessage = "Failed to validate token")
    }

    fun generateToken(
        userId: UserId,
        expiredAt: LocalDateTime,
        otpId: OtpId? = null,
        accountId: String? = null,
        roleId: String? = null,
        boardId: String? = null
    ): Mono<Token> {
        return idGeneratorService.generateId(IdType.TOKEN_ID)
            .flatMap { tokenId ->
                tokenRepository.save(
                    Token.generate(
                        tokenId = tokenId,
                        userId = userId,
                        expiredAt = expiredAt,
                        otpId = otpId,
                        accountId = accountId,
                        roleId = roleId,
                        boardId = boardId
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

    fun updateToken(updateTokenRequest: UpdateTokenRequest, tokenString: String): Mono<Token> {
        return tokenRepository.findByValueAndExpiredAtAfter(tokenString)
            .flatMap { token ->
                accountServiceGateway.isValidAccountAndRole(
                    userId = token.userId,
                    accountId = updateTokenRequest.projectId,
                    roleId = updateTokenRequest.roleId
                )
                    .flatMap {
                        generateToken(
                            userId = token.userId,
                            expiredAt = token.expiredAt,
                            otpId = null,
                            accountId = updateTokenRequest.projectId,
                            roleId = updateTokenRequest.roleId
                        )
                    }
            }
    }

    fun logout(token: String, userAuthenticationData: UserAuthenticationData): Mono<Token> {
        return tokenRepository.findByValue(token)
            .flatMap {
                tokenRepository.save(it.setExpired())
            }
            .auditOnSuccess(mqttPublisher = mqttPublisher, event = AuditEvent.LOG_OUT)
            .auditOnError(mqttPublisher = mqttPublisher, event = AuditEvent.LOG_OUT)
            .logOnSuccess(message = "Successfully logged out user")
            .logOnError(errorMessage = "Failed to log out user")
    }

    fun generateTokenForBoard(boardId: String, authenticationData: UserAuthenticationData): Mono<Token> {
        return tokenRepository.findByBoardIdAndExpiredAtAfter(boardId)
            .switchIfEmpty {
                accountServiceGateway.isValidBoard(boardId, authenticationData)
                    .flatMap {
                        generateToken(
                            expiredAt = LocalDateTime.now().plusYears(100),
                            accountId = authenticationData.accountId,
                            roleId = "00004",
                            boardId = boardId,
                            userId = "Board_$boardId"
                        )
                    }
            }
    }

    fun updateTokenForBoard(boardId: String, authenticationData: UserAuthenticationData): Mono<Token> {
        return tokenRepository.deleteByBoardId(boardId)
            .flatMap {
                generateTokenForBoard(boardId, authenticationData)
            }
    }
}
