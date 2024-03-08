package com.robotutor.iot.utils.filters

import com.robotutor.iot.logging.serializer.DefaultSerializer
import com.robotutor.iot.utils.models.UserAuthenticationData
import com.robotutor.iot.utils.utils.createMono
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Configuration
class AuthenticationDataResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == UserAuthenticationData::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        return createMono(exchange)
            .map { serverWebExchange ->
                val userAuthenticationData = serverWebExchange.request.headers.getFirst(AUTHORIZATION_HEADER_KEY)!!
                DefaultSerializer.deserialize(userAuthenticationData, UserAuthenticationData::class.java)
            }
    }
}
