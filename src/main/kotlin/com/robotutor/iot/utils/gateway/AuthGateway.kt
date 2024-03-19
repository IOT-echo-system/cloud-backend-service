package com.robotutor.iot.utils.gateway

import com.robotutor.iot.auth.controllers.AuthController
import com.robotutor.iot.utils.gateway.views.UserAuthenticationResponseData
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AuthGateway(private val authController: AuthController) {
    fun validate(token: String?): Mono<UserAuthenticationResponseData> {
        return authController.validateToken(token = token ?: "")
            .map {
                UserAuthenticationResponseData(userId = it.userId, projectId = it.projectId, roleId = it.roleId)
            }
    }
}
