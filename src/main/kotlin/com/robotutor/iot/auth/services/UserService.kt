package com.robotutor.iot.auth.services

import com.robotutor.iot.auth.controllers.view.UserLoginRequest
import com.robotutor.iot.auth.controllers.view.UserSignUpRequest
import com.robotutor.iot.auth.exceptions.IOTError
import com.robotutor.iot.auth.models.IdType
import com.robotutor.iot.auth.models.UserDetails
import com.robotutor.iot.auth.models.UserId
import com.robotutor.iot.auth.repositories.UserRepository
import com.robotutor.iot.logging.logOnError
import com.robotutor.iot.logging.logOnSuccess
import com.robotutor.iot.mqtt.models.AuditEvent
import com.robotutor.iot.mqtt.services.MqttPublisher
import com.robotutor.iot.utils.audit.auditOnError
import com.robotutor.iot.utils.audit.auditOnSuccess
import com.robotutor.iot.utils.exceptions.BadDataException
import com.robotutor.iot.utils.exceptions.DataNotFoundException
import com.robotutor.iot.utils.services.IdGeneratorService
import com.robotutor.iot.utils.utils.createMono
import com.robotutor.iot.utils.utils.createMonoError
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class UserService(
    private val userRepository: UserRepository,
    private val idGeneratorService: IdGeneratorService,
    private val passwordEncoder: PasswordEncoder,
    private val mqttPublisher: MqttPublisher
) {
    fun register(userDetails: UserSignUpRequest): Mono<UserDetails> {
        return userRepository.existsByEmail(userDetails.email)
            .flatMap {
                if (it) {
                    Mono.error(BadDataException(IOTError.IOT0101))
                } else {
                    idGeneratorService.generateId(IdType.USER_ID)
                        .flatMap { userId ->
                            val user = UserDetails.from(
                                userId = userId,
                                userDetails = userDetails,
                                password = passwordEncoder.encode(userDetails.password)
                            )
                            userRepository.save(user)
                                .auditOnSuccess(
                                    mqttPublisher = mqttPublisher,
                                    event = AuditEvent.SIGN_UP,
                                    userId = userId
                                )
                        }
                        .logOnSuccess(
                            message = "Successfully registered new User",
                            searchableFields = mapOf("email" to userDetails.email)
                        )
                        .logOnError(
                            errorMessage = "Failed to register new User",
                            searchableFields = mapOf("email" to userDetails.email)
                        )

                }
            }
    }

    fun verifyCredentials(userDetails: UserLoginRequest): Mono<UserDetails> {
        return userRepository.findByEmail(userDetails.email)
            .flatMap { details ->
                val matches = passwordEncoder.matches(userDetails.password, details.password)
                if (matches) {
                    createMono(details)
                        .auditOnSuccess(
                            mqttPublisher = mqttPublisher,
                            event = AuditEvent.VERIFY_PASSWORD,
                            userId = details.userId
                        )
                } else {
                    createMonoError<UserDetails>(BadDataException(IOTError.IOT0102))
                        .auditOnError(
                            mqttPublisher = mqttPublisher,
                            event = AuditEvent.VERIFY_PASSWORD,
                            userId = details.userId
                        )
                }
            }
            .switchIfEmpty {
                createMonoError(BadDataException(IOTError.IOT0102))
            }

    }

    fun resetPassword(userId: UserId, password: String): Mono<UserDetails> {
        return userRepository.findByUserId(userId)
            .flatMap {
                userRepository.save(it.updatePassword(passwordEncoder.encode(password)))
            }
            .auditOnSuccess(mqttPublisher = mqttPublisher, event = AuditEvent.RESET_PASSWORD, userId = userId)
            .auditOnError(mqttPublisher = mqttPublisher, event = AuditEvent.RESET_PASSWORD, userId = userId)
            .logOnSuccess(message = "Successfully updated password")
            .logOnError(errorMessage = "Failed to update password")
    }

    fun resetPassword(userId: UserId, currentPassword: String, password: String): Mono<UserDetails> {
        return userRepository.findByUserId(userId)
            .flatMap {
                this.verifyCredentials(UserLoginRequest(email = it.email, password = currentPassword))
            }
            .flatMap {
                userRepository.save(it.updatePassword(passwordEncoder.encode(password)))
            }
            .auditOnSuccess(mqttPublisher = mqttPublisher, event = AuditEvent.RESET_PASSWORD, userId = userId)
            .auditOnError(mqttPublisher = mqttPublisher, event = AuditEvent.RESET_PASSWORD, userId = userId)
            .logOnSuccess(message = "Successfully updated password")
            .logOnError(errorMessage = "Failed to update password")
    }

    fun getUserByEmail(email: String): Mono<UserDetails> {
        return userRepository.findByEmail(email)
            .switchIfEmpty {
                createMonoError(DataNotFoundException(IOTError.IOT0106))
            }
    }
}

