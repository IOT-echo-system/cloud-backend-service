package com.robotutor.iot.utils.filters

import com.robotutor.iot.utils.exceptions.AccessDeniedException
import com.robotutor.iot.utils.exceptions.IOTError
import com.robotutor.iot.utils.filters.annotations.RequirePolicy
import com.robotutor.iot.utils.gateway.PolicyGateway
import com.robotutor.iot.utils.models.BoardAuthenticationData
import com.robotutor.iot.utils.models.UserAuthenticationData
import com.robotutor.iot.utils.utils.createMono
import com.robotutor.iot.utils.utils.createMonoError
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Configuration
class BoardAuthenticationDataResolver(private val policyGateway: PolicyGateway) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == BoardAuthenticationDataResolver::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        return Mono.deferContextual { context ->
            val annotation = parameter.getMethodAnnotation(RequirePolicy::class.java)
            val userAuthenticationData = context.get(UserAuthenticationData::class.java)
            val boardAuthenticationData = BoardAuthenticationData.from(userAuthenticationData)
            if (annotation != null) {
                policyGateway.getPolicies(userAuthenticationData)
                    .map { userAccountPoliciesResponseData ->
                        userAccountPoliciesResponseData.policies.any { it.name == annotation.policyName }
                    }
                    .flatMap {
                        if (it) {
                            createMono(boardAuthenticationData)
                        } else {
                            createMonoError(AccessDeniedException(IOTError.IOT0103))
                        }
                    }
            } else {
                createMono(boardAuthenticationData)
            }
        }
    }
}
